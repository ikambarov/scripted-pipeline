node('terraform'){
    stage("Test"){
        sh """
            terraform version
        """
    }
}
