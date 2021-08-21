node("docker"){
    stage("Check"){
        sh '''
            docker version
        '''
    }
}