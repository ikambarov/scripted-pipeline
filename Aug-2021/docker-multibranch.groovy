node('docker'){
    stage('Pull'){
        git 'https://github.com/ikambarov/Flaskex-docker.git'
    }
    
    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        if( env.BRANCH_NAME == "master" ){
            tag = "latest"
        }
        else {
            tag = env.BRANCH_NAME
        }

        stage('Build'){
            sh """
                docker build -t ${USERNAME}/flaskex:${tag} .
            """
        }

        stage('Push'){
            sh """
                docker login -u ${USERNAME}  -p ${PASSWORD}
                docker push ${USERNAME}/flaskex:${tag}
            """
        }
    }
}