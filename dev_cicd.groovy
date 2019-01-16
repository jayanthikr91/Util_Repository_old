def execute(){
try{
stage('checkout')

{
echo "${prop}"
def giturl=prop.GIT_URL
echo "giturl=${giturl}"
git "${giturl}"

}

stage ('build'){

def buildcmd="mvn clean install -Dv=${BUILD_NUMBER}"
echo "${buildcmd}"
sh "${buildcmd}"

}

stage('code analysis'){
    echo "${prop}"
    def mvncmd=prop.MAVEN_SONAR_CMD
    def sonarurl=prop.SONAR_URL
    def url=mvncmd+sonarurl
    echo "${url}"
    sh "${url}"
}
stage('Artifactory upload')
{
def server = Artifactory.server "${prop.ARTIFACT_ID}"
def uploadSpec = """{
 	
"files":[
{
"pattern":"target/*.war",
"target":"hello/"
}
]
}"""
server.upload(uploadSpec)
}
stage ('Final deploy'){
def src=prop.SRC_DEPLOY_LOC
def dest=prop.DEST_DEPLOY_LOC
def deployCmd="scp "+"${src}"+" "+"${dest}"
echo "${deployCmd}"
sh "${deployCmd}"
}
stage('email'){
    emailext body: 'Build is successful', subject: 'email notification', to: 'jayanthikr91@gmail.com'
  }
  }
  catch(Exception e){
  stage('email'){
    emailext body: 'Build is unsuccessful', subject: 'email notification', to: 'jayanthikr91@gmail.com'
  }
  }


}