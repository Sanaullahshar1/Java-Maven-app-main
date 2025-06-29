def buildVersion() {
    echo 'Incrementing app version..'
    sh 'mvn build-helper:parse-version versions:set \
        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
        versions:commit'
    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = matcher[0][1]
    env.IMAGE_NAME = "$version-$BUILD_NUMBER"

}

def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
} 

def buildImage() {
    echo "building the docker image...."
    withCredentials([usernamePassword(credentialsId: 'ACR', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "docker build -t ${ACR_REPO}:${IMAGE_NAME} ."
        sh "echo $PASS | docker login ${ACR_LOGIN_SERVER} -u $USER --password-stdin"
        sh "docker push ${ACR_REPO}:${IMAGE_NAME}"
    }
} 

def deployApp() {
    withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'K8s', namespace: '', restrictKubeConfigAccess: false, serverUrl: '') {
        sh "envsubst < java-app.yaml | kubectl apply -f -"      //Install gettext-base(envsubst) in jenkins container, create ACR secret 
}
}

def commitVersion() {
    echo 'commiting version bump...'
    withCredentials([usernamePassword(credentialsId: 'gitlab-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "git remote set-url origin https://${USER}:${PASS}@github.com/Sanaullahshar1/Java-Maven-app.git"
        sh 'git add .'
        sh 'git commit -m "ci: version bump"'
        sh 'git push origin HEAD:jenkins/kube'
    }

} 

return this
