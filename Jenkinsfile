pipeline {
  agent any
  environment {
    PROJECT_NAME = "daedalus"
    PROJECT_DIR = "daedalus"
    BLACKDUCK_PROJECT = "IMS_ComCept_ALE"
    registry = "docker-local.artifactory/daedalus"
    FORTIFY_URL = "http://cpt-ssc:8080/ssc/"
    FORTIFY_TOKEN = "90d7f78d-0844-43c5-9d3c-4436183edb78" // IF you have account in FOrtifySSC Portal, you can create and use your own token also.
    registryCredential = ''
    dockerImage = ''
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr:'14'))
  }
  stages {
    stage('Build') {
      parallel {
        stage('Maven: mvn compile') {
          when {
            not {
              anyOf {
                branch 'master'
                branch 'develop'
                branch 'development'
                branch 'release/*'
              }
            }
          }
          steps {
            sh 'mvn -B -f ${PROJECT_DIR}/pom.xml clean compile install -Dbuild.number=${BUILD_NUMBER}'
          }
        }
        stage('Maven: mvn compile deploy') {
          when {
            anyOf {
              branch 'master'
              branch 'develop'
              branch 'development'
              branch 'release/*'
            }
          }
          steps {
            script {
              def server = Artifactory.server 'artifactory'
              def buildInfo = Artifactory.newBuildInfo()
              buildInfo.env.capture = true
              def rtMaven = Artifactory.newMavenBuild()
              rtMaven.opts = "-Denv=dev"
              rtMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
              rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server

              rtMaven.run pom: '${PROJECT_DIR}/pom.xml', goals: '-B compile install -Dbuild.number=${BUILD_NUMBER}', buildInfo: buildInfo

              buildInfo.retention maxBuilds: 10, maxDays: 7, deleteBuildArtifacts: true
              // Publish build info
              server.publishBuildInfo buildInfo
            }
          }
        }
      }
    }
    stage('Code Quality Scanning') {
      parallel{
        stage('SonarQube') {
          steps {
            script {
              withSonarQubeEnv('SonarQube') {
                sh 'mvn -B -f ${PROJECT_DIR}/pom.xml sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL}'
              }
              sleep(10)
              def qualityGate = waitForQualityGate()
              if (qualityGate.status != "OK") {
                error "Pipeline aborted due to quality gate coverage failure: ${qualityGate.status}"
              }
            }
          }
        }
        stage('Fortify') {
          when {
            anyOf {
              branch 'master'
              branch 'develop'
              branch 'development'
              branch 'release/*'
            }
          }
          steps {
            sh 'cd ${WORKSPACE}'
            sh '$FORTIFY_HOME/bin/sourceanalyzer -b ${BUILD_ID} -clean'              
            sh '$FORTIFY_HOME/bin/sourceanalyzer -b ${BUILD_ID} -jdk 1.8 -java-build-dir classes .'
            sh '$FORTIFY_HOME/bin/sourceanalyzer -b ${BUILD_ID} -scan -f ${BUILD_TAG}.fpr'	
            sh '$FORTIFY_HOME/bin/FPRUtility -information -project ${BUILD_TAG}.fpr  -categoryIssueCounts -outputFormat CSV -f ${BUILD_TAG}.csv'
            //sh '$FORTIFY_HOME/bin/fortifyupdate -url=http://cpt-ssc:8080/ssc'
//            sh '$FORTIFY_HOME/bin/fortifyclient uploadFPR -f ${BUILD_TAG}.fpr -application ${PROJECT_NAME} -applicationVersion 1.0 -url ${FORTIFY_URL} -authtoken ${FORTIFY_TOKEN}'      
            fortifyUpload appName: 'daedalus', appVersion: '1.0', failureCriteria: '', filterSet: '', pollingInterval: '', resultsFile: '${BUILD_TAG}.fpr'

            
          }
        }
        stage('Black Duck') {
          when {
            anyOf {
              branch 'master'
              branch 'develop'
              branch 'development'
              branch 'release/*'
            }
          }
          steps {
            synopsys_detect '--detect.project.name=${BLACKDUCK_PROJECT} --detect.project.version.name=code-scan-${JOB_NAME} --blackduck.trust.cert=true'
          }	
        }
	  }
    }
  }
  post {
    always {
      script {
        currentBuild.result = currentBuild.result ?: 'SUCCESS'
        notifyBitbucket()
      }
    }
  }
  tools {
    maven 'Maven 3.3.9'
    jdk 'OpenJDK11'
  }
}
