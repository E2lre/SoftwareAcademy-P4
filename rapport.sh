Echo ****************************Surfire****************************
mvn surefire-report:report
Echo ****************************Jacoco****************************
mvn jacoco:report
Echo ****************************Spotbugs****************************
mvn site
Echo ****************************Chrome****************************
Chrome file:///C:/Devenv/git/P4/SoftwareAcademy-P4/target/site/jacoco/index.html