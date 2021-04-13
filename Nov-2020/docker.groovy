node("docker"){
    stage("Pull"){
        git url: 'https://github.com/ikambarov/Flaskex-docker.git'
    }
    stage("Build"){
        sh'docker build -t ikambarov/flaskex .'
    }
    stage("Push"){
        withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            sh """
                docker login -u ${USERNAME} -p ${PASSWORD} 
                docker push ikambarov/flaskex
            """
        }   
    }
    stage("Run"){
        sh "docker run -d -p 5000:5000 ikambarov/flaskex"
    }
}