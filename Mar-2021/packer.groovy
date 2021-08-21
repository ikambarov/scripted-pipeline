node('packer'){
    stage("Check"){
        sh '''
            packer version
        '''
    } 
}
