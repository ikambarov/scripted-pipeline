node("terraform"){
    stage("terraform"){
        sh '''
            terraform version
        '''
    }
}