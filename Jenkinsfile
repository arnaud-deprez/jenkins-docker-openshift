#!/usr/bin/env groovy
// Best practices: https://github.com/jenkinsci/pipeline-examples/blob/master/docs/BEST_PRACTICES.md

def getProjectName() {
  if (env.BRANCH_NAME != env.MASTER_BRANCH)
    return "cicd-br-$env.BRANCH_NAME"
  return env.STAGING_PROJECT
}

def ensureProject(name) {
  echo "Ensure project ${getProjectName()} exists..."  
  try {
    openshift.withCluster() {
      openshift.newProject(name)
      if (name == env.STAGING_PROJECT) {
        echo "Allow jenkins service account from $env.STAGING_PROJECT to edit cicd project"
        openshift.policy('add-role-to-user', 'edit', "system:serviceaccount:$env.STAGING_PROJECT:jenkins", '--rolebinding-name=jenkins_staging_edit', '-n', 'cicd')
      }
    }
  } catch (e) {
    echo "WARN: Cannot create project because: $e"
  }
}

def inProject = { name, block -> 
  openshift.withCluster() {
    openshift.withProject(name) {
      echo "Run in project $name"
      block()
    }
  }
}

def buildImage(image) {
  def build = openshift.process(readFile("$image/openshift/build-template.yml"))
  def buildDockerConfig = openshift.apply(build).narrow('bc')
  echo "Start building docker image: $image"
  buildDockerConfig.startBuild("--from-dir=.")
}

def deployMaster(type, options = [:]) {
  def project = openshift.project()
  echo "Configure $type jenkins master in project $project"
  def deployment = (type == 'persistent') ?
    openshift.process(readFile('2/openshift/jenkins-persistent-template.yml'),
      '-p', 'JENKINS_IMAGE_STREAM_TAG=jenkins-custom:latest', 
      '-p', "NAMESPACE=$project", 
      '-p', "VOLUME_CAPACITY=${options.get('volumeCapacity', '5Gi')}") : 
    openshift.process(readFile('2/openshift/jenkins-ephemeral-template.yml'), 
      '-p', 'JENKINS_IMAGE_STREAM_TAG=jenkins-custom:latest', 
      '-p', "NAMESPACE=$project")
  
  // Do not replay PersistenceVolumeClaim if already exists
  deployment = deployment.findAll {
    openshift.selector('PersistentVolumeClaim', 'jenkins').exists() ?
      it.kind != 'PersistentVolumeClaim' : 
      true
  }
  echo "Apply: $deployment"
  openshift.apply(deployment).narrow('dc').withEach { dc ->
    timeout(time: 10, unit: 'MINUTES') {
      dc.untilEach(1) {
        it.rollout().status().out.contains('successfully rolled out')
      }
    }
  }
}

def configureSlave(slave, type, options = [:]) {
  def project = openshift.project()
  echo "Configure $type jenkins slave $slave in project $project"
  def cfg = (type == 'persistent' ? 
    openshift.process(readFile("agent-${slave}/openshift/agent-config-${type}.yml"), 
      '-p', "IMAGE=imagestreamtag:$project/${options.get('image')}:latest", 
      '-p', "VOLUME_CAPACITY=${options.get('volumeCapacity', '10Gi')}") : 
    openshift.process(readFile("agent-${slave}/openshift/agent-config-${type}.yml"), 
      '-p', "IMAGE=imagestreamtag:$project/${options.get('image')}:latest"))
  openshift.apply(cfg)
}

def deleteProjectIf(project, predicate = { it -> true }) {
  if (predicate(project)) {
    echo "Clean and delete project ${project}"
    openshift.withCluster() {
      openshift.withProject(project) {
        def result = openshift.raw('delete', 'all,pvc', '--all')
        echo "Delete output: $result.out"
      }
      def result = openshift.delete('project', project)
      echo "Delete project: $result.out"
    }
  }
}

def tmpEnvPredicate = { name -> name != 'cicd' && name != env.STAGING_PROJECT }

pipeline {
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
    disableConcurrentBuilds()
  }

  agent { label 'maven' }

  environment {
    MASTER_BRANCH='master'
    STAGING_PROJECT='cicd-staging'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        ensureProject(getProjectName())
      }
    }
    stage('Build') {
      // As Builds are already performed in other Pods, there is no need to do an extra parallelization here.
      steps {
        script {
          inProject(getProjectName()) {
            ['2', 'agent-gradle', 'agent-nodejs'].collect {
              buildImage(it)
            }.each { build ->
              echo "Verify build: ${build.name()}..."
              timeout(time: 10, unit: 'MINUTES') {
                build.untilEach(1) {
                  it.object().status.phase == "Complete"
                }
              }
            }
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        script {
          inProject(getProjectName()) {
            if (env.BRANCH_NAME == env.MASTER_BRANCH) {
              deployMaster('persistent', [volumeCapacity: '1Gi'])
              openshift.apply(openshift.process(readFile('openshift/pipeline-promote-template.yml')))
            }
            else {
              deployMaster('ephemeral')
            }
            configureSlave('gradle', 'ephemeral', [image: 'jenkins-agent-gradle'])
            configureSlave('nodejs', 'ephemeral', [image: 'jenkins-agent-nodejs'])
            configureSlave('gradle-nodejs', 'ephemeral', [image: 'jenkins-agent-nodejs'])
          }
        }
      }
    }
    stage('Test') {
      failFast true
      parallel {
        stage('master') {
          steps {
            script {
              dir('2/test/integration-tests') {
                sh "mvn -B test -Dservice=jenkins.${getProjectName()}.svc.cluster.local -Dport=80"
              }
            }
          }
        }
        stage('Slaves') {
          // As test pipeline are already performed in other Pods, there is no need to do an extra parallelization here.
          steps {
            script {
              def project = getProjectName()
              inProject(project) {
                echo "Run test agent-test-pipeline in project $project"
                def buildCfg = openshift.apply(readFile("openshift/agent-test-pipeline.yml")).narrow('bc')
                def build = buildCfg.startBuild()
                echo "Verify test pipeline ${build.name()}..."
                timeout(time: 10, unit: 'MINUTES') {
                  build.untilEach(1) {
                    it.object().status.phase == "Complete"
                  }
                }
              }
            }
          }
        }
      }
    }
    stage('Promote') {
      when { branch env.MASTER_BRANCH }
      steps {
        script {
          def project = getProjectName()
          inProject(project) {
            echo "Promoting Jenkins from $project to cicd... This jenkins will be replaced by the new one..."
            openshift.startBuild('jenkins-promote-pipeline', '-e', "BRANCH_NAME=$env.BRANCH_NAME")
          }
        }
      }
    }
  }
  post {
    aborted {
      echo "Pipeline ABORTED!"
      deleteProjectIf(getProjectName(), tmpEnvPredicate)
    }
    success {
      echo "Pipeline SUCCESS!"
      deleteProjectIf(getProjectName(), tmpEnvPredicate)
    }
  }
}