node {
    stage("test") {
        sh "ansible --version"
    }
}