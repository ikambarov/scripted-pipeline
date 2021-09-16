node("worker1"){
    stage("Version"){
        sh "terraform version"
    }
}