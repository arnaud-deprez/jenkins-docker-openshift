#!/usr/bin/env groovy
// Best practices: https://github.com/jenkinsci/pipeline-examples/blob/master/docs/BEST_PRACTICES.md

def getTestProjectName() {
  return env.STAGING_PROJECT
}

def ensureProject(name) {
  echo "Ensure project ${name} exists..."  
  try {
    sh "oc new-project ${name}"
    if (name == env.STAGING_PROJECT) {
      echo "Allow jenkins service account from ${name} to edit cicd project"
      sh "kubectl -n cicd create rolebinding jenkins_staging_admin --clusterrole=admin --serviceaccount=${name}:jenkins --dry-run -o yaml | kubectl -n cicd apply -f -"
    }
  } catch (e) {
    echo "WARN: Cannot create project because: $e"
  }
}

def inProject = { name, block -> 
  openshift.withCluster() {
    openshift.withProject(name) {
      block()
    }
  }
}

def agentLabel = 'base'

pipeline {
  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
    disableConcurrentBuilds()
  }

  agent { label agentLabel }

  environment {
    MASTER_BRANCH='master'
    STAGING_PROJECT='cicd-staging'
  }

  stages {
    stage('Build') {
      // As Builds are already performed in other Pods, there is no need to do an extra parallelization here.
      steps {
        container(agentLabel) {
          script {
            ensureProject(env.STAGING_PROJECT)
            sh """
              export NAMESPACE=$env.STAGING_PROJECT
              make --directory=charts/openshift-build --environment-overrides apply
              make openshiftBuild
            """
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        container(agentLabel) {
          sh """
            export NAMESPACE=$env.STAGING_PROJECT
            make --directory=charts/jenkins-openshift --environment-overrides applyFromBuild
            okd-verify-workload $env.STAGING_PROJECT deployment/jenkins
          """
        }
      }
    }
    stage('Test') {
      failFast true
      parallel {
        stage('master') {
          steps {
            container(agentLabel) {
              script {
                echo "test master"
                /* dir('2/test/integration-tests') {
                  sh "mvn -B test -Dservice=jenkins.${getProjectName()}.svc.cluster.local -Dport=80"
                } */
              }
            }
          }
        }
        stage('Slaves') {
          // As test pipeline are already performed in other Pods, there is no need to do an extra parallelization here.
          steps {
            container(agentLabel) {
              sh """
                export NAMESPACE=$env.STAGING_PROJECT
                make --environment-overrides openshiftTestPipeline
              """
            }
          }
        }
      }
    }
    stage('Promote') {
      when { branch env.MASTER_BRANCH }
      steps {
        container(agentLabel) {
          echo "Promoting Jenkins from $env.STAGING_PROJECT to cicd... This jenkins will be replaced by the new one..."
          sh """
            oc process -f openshift/promote-pipeline-template.yaml | oc -n $env.STAGING_PROJECT apply -f -
            oc -n $env.STAGING_PROJECT start-build jenkins-promote-pipeline
          """
        }
      }
    }
  }
  post {
    aborted {
      echo "Pipeline ABORTED!"
    }
    success {
      echo "Pipeline SUCCESS!"
    }
  }
}