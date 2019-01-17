def execute(prop){
try{
def pom    
stage('checkout')

{
echo "in checkout"
echo "${prop.GIT_URL}"
def giturl=prop.GIT_URL
echo "giturl=${giturl}"
git "${giturl}"
pom=readMavenPom  file: prop.POM_FILE
}

stage ('build'){

echo "${prop.MAVEN_CMD}"
sh "${prop.MAVEN_CMD}"

}

stage('code analysis'){
    echo "${prop.MAVEN_SONAR_CMD}+${prop.SONAR_URL}"
    sh "${prop.MAVEN_SONAR_CMD}+${prop.SONAR_URL}"
}
stage('Artifactory upload')
{
def server = Artifactory.server "${prop.ARTIFACT_ID}"
def uploadSpec = """{
 	
"files":[
{
"pattern":"${prop.WAR_PATTERN}",
"target":"hello/${pom.artifactId}/${pom.version}.${BUILD_NUMBER}/"
}
]
}"""
server.upload(uploadSpec)
}
stage ('Final deploy'){
echo "${prop.SECURE_COPY_CMD}+${prop.SRC_DEPLOY_LOC}+${prop.DEST_DEPLOY_LOC}"
sh "${prop.SECURE_COPY_CMD}+${prop.SRC_DEPLOY_LOC}+${prop.DEST_DEPLOY_LOC}"
}
stage('success email'){
    emailext body: 'Build is successful', subject: 'email notification', to: 'Jayanthi.Ravinathan@mindtree.com'
  }
  }
  catch(Exception e){
  stage('failure email'){
    emailext body: 'Build is unsuccessful', subject: 'email notification', to: 'Jayanthi.Ravinathan@mindtree.com'
  }
  }


}
return this
