package com.smartchain.core.hyperledger;

import com.smartchain.core.docker.DockerService;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;

import java.util.Arrays;
import java.util.List;

public class DeploymentService {


    private static final String HYPERLEDGER_FABRIC_COUCHDB_IMAGE = "hyperledger/fabric-couchdb:x86_64-1.0.0";
    private static final String HYPERLEDGER_FABRIC_ORDERER_IMAGE = "hyperledger/fabric-orderer:x86_64-1.0.0";
    private static final String HYPERLEDGER_FABRIC_PEER_IMAGE = "hyperledger/fabric-orderer:x86_64-1.0.0";
    private static final String HYPERLEDGER_COMPOSER_PLAYGROUND_IMAGE = "hyperledger/fabric-orderer:x86_64-1.0.0";

    private static final List<String> HYPERLEDGER_IMAGES = Arrays.asList(
            HYPERLEDGER_FABRIC_COUCHDB_IMAGE,
            HYPERLEDGER_FABRIC_ORDERER_IMAGE,
            HYPERLEDGER_FABRIC_PEER_IMAGE,
            HYPERLEDGER_COMPOSER_PLAYGROUND_IMAGE
    );

    private DockerService dockerService;

    public DeploymentService(){
        try {
            dockerService = new DockerService();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    private void createDockerServices(){
        String containerId;
        String containerName = "ca.org1.example.com";
        try {
            if(dockerService.isContainerCreated(containerName)) {
                containerId = dockerService.getContainerId(containerName);
            }else{
                ContainerCreation containerCreation = dockerService.getDockerClient().createContainer(ContainerConfigurations.PEER_CA_CONFIGURATION, containerName);
                containerId = containerCreation.id();
            }
            dockerService.getDockerClient().startContainer(containerId);
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runCaContainer() throws DockerException, InterruptedException {
        String containerName = "ca.org1.example.com";
        String containerId;
        if(dockerService.isContainerCreated(containerName)) {
            containerId = dockerService.getContainerId(containerName);
        }else {
            ContainerCreation containerCreation = dockerService.getDockerClient().createContainer(ContainerConfigurations.PEER_CA_CONFIGURATION, containerName);
            containerId = containerCreation.id();
        }
        dockerService.getDockerClient().startContainer(containerId);
    }

    private void runContainer(ContainerConfig containerConfig, String containerName) throws DockerException, InterruptedException {
        String containerId;
        if(dockerService.isContainerCreated(containerName)) {
            containerId = dockerService.getContainerId(containerName);
        }else {
            ContainerCreation containerCreation = dockerService.getDockerClient().createContainer(containerConfig, containerName);
            containerId = containerCreation.id();
        }
        dockerService.getDockerClient().startContainer(containerId);
    }

    public void redeployHyperledgerFabricNetwork() throws DockerException, InterruptedException {
//        try {
//            dockerService.removeAllImages();
//        } catch (DockerException | InterruptedException e) {
//            e.printStackTrace();
//        }

        // cleanup
        dockerService.removeAllRunningContainers();

        // deploy certificates

        // pull hyperledger images
        dockerService.pullImages(HYPERLEDGER_IMAGES);

        // start hyperledger from composer
        createDockerServices();

        // create channel

        // join peer to the channel

    }

    public static void main(String[] args) {
        DeploymentService deploymentService = new DeploymentService();
        deploymentService.redeployHyperledgerFabricNetwork();
    }
}
//            ExecCreation execCreation = dockerService.getDockerClient().execCreate(containerId, new String[]{"sh", "-c", "'fabric-ca-server start --ca.certfile /etc/hyperledger/fabric-ca-server-config/ca.org1.example.com-cert.pem --ca.keyfile /etc/hyperledger/fabric-ca-server-config/19ab65abbb04807dad12e4c0a9aaa6649e70868e3abd0217a322d89e47e1a6ae_sk -b admin:adminpw -d'"});
