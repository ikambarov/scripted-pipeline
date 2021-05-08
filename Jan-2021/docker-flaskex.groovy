node("docker") {
    withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', passwordVariable: 'password', usernameVariable: 'username')]) {
        stage('Pull SCM') {
            git url: 'https://github.com/ikambarov/Flaskex-docker.git'
        }

        stage("Docker Build") {
            sh "docker build -t ${username}/flaskex:latest  ."
        }

        stage("Docker Login") { 
            sh "docker login --username ${username} --password ${password}"
        }

        stage("Docker Push") {
            sh "docker push ${username}/flaskex:latest"
        }
    }
}
