node('ansible'){
    stage("Check"){
        sh '''
            ansible --version
        '''
    }
}
