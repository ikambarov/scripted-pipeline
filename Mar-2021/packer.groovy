node('packer'){
    sh '''
        packer version
    '''
}
