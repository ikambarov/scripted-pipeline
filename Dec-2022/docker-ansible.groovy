node('ansible'){
    stage("Ansible Version"){
        sh "ansible --version"
    }
}