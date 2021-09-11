properties([
    parameters([
        string(description: 'Provide Linux VM IP', name: 'IPADDRESS', trim: true)
        ])
    ])

node("worker1"){
    stage("Intall Epel"){
        withCredentials([sshUserPrivateKey(credentialsId: 'masterkey', keyFileVariable: 'SSHKEY', usernameVariable: 'SSHUSERNAME')]) {
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'hostname'
                """
        }        
    }
}