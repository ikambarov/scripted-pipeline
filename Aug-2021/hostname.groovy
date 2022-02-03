node('ansible'){
    stage("Hostname"){
        sh "hostname"
    }
}
