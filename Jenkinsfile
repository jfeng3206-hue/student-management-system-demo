pipeline {
    agent any

    options {
        skipStagesAfterUnstable()
        timestamps()
    }

    stages {
        stage('Test & Coverage') {
            steps {
                sh '''
                    export PATH=/opt/java/openjdk/bin:$PATH
                    export SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL:-jdbc:postgresql://host.docker.internal:5432/sms}
                    export SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME:-postgres}
                    export SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD:-pass}
                    chmod +x ./mvnw
                    ./mvnw clean verify -B
                '''
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { return env.SONAR_ENABLED == 'true' }
            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                        export PATH=/opt/java/openjdk/bin:$PATH
                        ./mvnw sonar:sonar -B
                    '''
                }
            }
        }

        stage('Quality Gate') {
            when {
                expression { return env.SONAR_ENABLED == 'true' }
            }
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build') {
            steps {
                sh '''
                    export PATH=/opt/java/openjdk/bin:$PATH
                    ./mvnw -DskipTests package -B
                '''
            }
        }

        stage('Deploy to EC2') {
            steps {
                sh '''
                    EC2_HOST=34.221.51.27
                    EC2_USER=ubuntu
                    SSH_KEY=/var/jenkins_home/springboot-demo.pem
                    JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar

                    scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$JAR_FILE" "$EC2_USER@$EC2_HOST:/home/ubuntu/app.jar.tmp"

                    ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$EC2_USER@$EC2_HOST" '
                        set -e

                        sudo docker stop sms || true
                        sudo docker rm sms || true

                        sudo pkill -f "^java -jar app.jar$" || true
                        sleep 3

                        mv /home/ubuntu/app.jar.tmp /home/ubuntu/app.jar
                        cd /home/ubuntu

                        set -a
                        . /home/ubuntu/sms.env
                        set +a

                        nohup java -jar app.jar > app.log 2>&1 &

                        sleep 15
                        curl -f -X POST "http://localhost:8080/v1/name/aggregation?name=Jenkins"
                    '
                '''
            }
        }
    }
}
