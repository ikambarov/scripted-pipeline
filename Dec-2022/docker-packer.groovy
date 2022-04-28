node('packer'){
    stage("Packer Version"){
        sh "packer version"
    }
}