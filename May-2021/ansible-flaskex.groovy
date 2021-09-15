properties([
    parameters([
        string(description: 'Provide Linux Machine', name: 'IPADDRESS', trim: true)
        ])
    ])
    
node("worker1"){
    stage("Pull repo"){
        git 'https://github.com/ikambarov/ansible-Flaskex.git'
    }

     withCredentials([sshUserPrivateKey(credentialsId: 'masterkey', keyFileVariable: 'SSHKEY', usernameVariable: 'SSHUSERNAME')]) {
        stage("Install Prerequisites"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSHKEY -i '${params.IPADDRESS},' prerequisites.yml
            """
        }
        
        stage("Pull Flaskex Repo"){
            withEnv(['FLASKEX_REPO=https://github.com/ikambarov/Flaskex.git', 'FLASKEX_BRANCH=master']) {
                sh """
                    export ANSIBLE_HOST_KEY_CHECKING=False
                    ansible-playbook --private-key $SSHKEY -i '${params.IPADDRESS},' pull_repo.yml
                """
            }            
        }

        stage("Install Python"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSHKEY -i '${params.IPADDRESS},' install_python.yml
            """
        }

        stage("Start App"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSHKEY -i '${params.IPADDRESS},' start_app.yml
            """
        }
    } 
}