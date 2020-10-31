node{
    properties([
        parameters([
            string(defaultValue: '', description: 'Provide Remote Node IP', name: 'node_ip', trim: true)
        ])
    ])

    if(node_ip.length() > 7 ){
        stage("Gi Clone Kellys Repo"){
            git url: 'https://github.com/ksalrin/anisble-springpetclinic.git'
        }
        
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-ssh-key', keyFileVariable: 'SSH_KEY', passphraseVariable: '', usernameVariable: 'SSH_USERNAME')]) {
            stage("Instll Prerequisites"){
                sh """
                    export ANSIBLE_HOST_KEY_CHECKING=False
                    ansible-playbook -i "${node_ip}," --private-key $SSH_KEY -u $SSH_USERNAME prerequisites.yml
                """ 
            } 
            stage("Pull Spring Petclinic App"){
                withEnv(['FLASKEX_REPO=https://github.com/ikambarov/spring-petclinic.git', 'FLASKEX_BRANCH=master']) {
                    sh """
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook -i "${node_ip}," --private-key $SSH_KEY -u $SSH_USERNAME pull_repo.yml
                    """
                }     
            } 
            stage("Install Java"){
                sh """
                    export ANSIBLE_HOST_KEY_CHECKING=False
                    ansible-playbook -i "${node_ip}," --private-key $SSH_KEY -u $SSH_USERNAME install_java.yml
                """    
            } 
            stage("Install Maven"){
                sh """
                    export ANSIBLE_HOST_KEY_CHECKING=False
                    ansible-playbook -i "${node_ip}," --private-key $SSH_KEY -u $SSH_USERNAME install_maven.yml
                """    
            } 
            stage("Build/Start App"){
                sh """
                    export ANSIBLE_HOST_KEY_CHECKING=False
                    ansible-playbook -i "${node_ip}," --private-key $SSH_KEY -u $SSH_USERNAME start_app.yml
                """    
            } 
        }  
    }
    else {
        error("Please provide a valid IP")
    }
}
