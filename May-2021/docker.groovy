node("docker"){
    def class="May"

    stage("Pull"){
        git 'https://github.com/ikambarov/Flaskex-docker.git'
    }
    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'HUBPASS', usernameVariable: 'HUBUSERNAME')]) {
        stage("Build"){
            sh '''
                docker build -t $HUBUSERNAME/flaskex .
            ''' 
        }

        stage("Push"){
            sh '''
                docker login -u $HUBUSERNAME -p $HUBPASS
                docker push $HUBUSERNAME/flaskex
            '''
        }

        stage("Deploy"){
            sh '''
                docker run -d -p 5000:5000 $HUBUSERNAME/flaskex
            '''
        }
    }    
}