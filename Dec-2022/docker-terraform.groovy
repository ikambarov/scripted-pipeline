node('terraform'){
    stage("Terraform Version"){
        sh "terraform version"
    }
}