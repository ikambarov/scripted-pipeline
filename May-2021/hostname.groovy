node("worker1"){
    stage("hostname"){
        sh '''
            hostname
        '''
    }
}

