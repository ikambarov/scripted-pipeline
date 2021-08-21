node("docker"){
    stage("pull code"){
        git "https://github.com/ikambarov/Flaskex-docker.git"
    }
    

    withCredentials([usernamePassword(credentialsId: 'docker-hub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
        stage("build"){
            sh '''
                docker build -t $dockerhubuser/flaskex .
            '''
        }
        
        stage("push"){
            
            sh '''
                docker login --username $dockerhubuser --password $dockerhubpass

                docker push $dockerhubuser/flaskex 
            '''
        } 
    }
}