properties([
    parameters([
        string(description: 'Provide Linux Machine', name: 'IPADDRESS', trim: true)
        ])
    ])

node('worker1'){
    withCredentials([sshUserPrivateKey(credentialsId: 'masterkey', keyFileVariable: 'SSHKEY', usernameVariable: 'SSHUSERNAME')]) {
        stage("Test"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible --private-key $SSHKEY -i '${params.IPADDRESS},' all -m ping
            """
        }
    } 
}