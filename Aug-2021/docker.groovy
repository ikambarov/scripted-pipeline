node('docker'){
    stage('Pull'){
        git 'https://github.com/ikambarov/Flaskex-docker.git'
    }
    
    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        stage('Build'){
            sh """
                docker build -t ${USERNAME}/flaskex:v1 .
            """
        }

        stage('Push'){
            sh """
                docker login -u ${USERNAME}  -p ${PASSWORD}
                docker push ${USERNAME}/flaskex:v1
            """
        }
    }
}