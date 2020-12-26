node("docker"){
    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/Flaskex-docker.git'
    }
    
    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', passwordVariable: 'REGISTRY_PASSWORD', usernameVariable: 'REGISTRY_USERNAME')]) {
        stage("Docker Build"){
            sh "docker build -t ${REGISTRY_USERNAME}/flaskex  ."
        }
        
        stage("Docker Login"){
            sh "docker login -u ${REGISTRY_USERNAME} -p '${REGISTRY_PASSWORD}'"
        }

        stage("Docker Push"){
            sh "docker push ${REGISTRY_USERNAME}/flaskex"
        }

        stage("Cleanup"){
            sh "docker rmi ${REGISTRY_USERNAME}/flaskex"
        }
    }
}