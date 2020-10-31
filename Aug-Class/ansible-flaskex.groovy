node{
    properties([
        parameters([
            string(defaultValue: '', description: 'Provide Remote Node IP', name: 'node_ip', trim: true)
        ])
    ])

    if(node_ip.length() > 7 ){
        stage("Git Pull"){
            git url: 'https://github.com/ikambarov/ansible-Flaskex.git'
        }

        
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-ssh-key', keyFileVariable: 'SSH_KEY', passphraseVariable: '', usernameVariable: 'SSH_USERNAME')]) {
            stage("Ping"){
                withEnv(['FLASKEX_REPO=https://github.com/ikambarov/Flaskex.git', 'FLASKEX_BRANCH=master']) {
                    sh """
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook -i "${node_ip}," --private-key $SSH_KEY -u $SSH_USERNAME main.yml
                    """
                }     
            } 
        }  
    }
    else {
        error("Please provide a valid IP")
    }
}
