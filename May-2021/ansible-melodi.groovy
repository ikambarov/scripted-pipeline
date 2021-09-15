properties([
    parameters([
        string(description: 'Provide Linux Machine', name: 'IPADDRESS', trim: true)
        ])
    ])
    
node("worker1"){
    stage("Pull repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }

     withCredentials([sshUserPrivateKey(credentialsId: 'masterkey', keyFileVariable: 'SSHKEY', usernameVariable: 'SSHUSERNAME')]) {
        stage("Install Melodi"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSHKEY -i '${params.IPADDRESS},' main.yml
            """
        }
    } 
}