# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "cloudera-quickstart-vm-4.4.0-1-1.0.0"

  # The url from where the 'config.vm.box' box will be fetched if it
  # doesn't already exist on the user's system.
  config.vm.box_url = "http://repo.release.cerner.corp/nexus/content/repositories/vagrant/com/cerner/vagrant/cloudera-quickstart-vm-4.4.0-1/1.0.0/cloudera-quickstart-vm-4.4.0-1-1.0.0.box"

  # Sync the directory we are in to the VM
  config.vm.synced_folder ".", "/home/cloudera/mapreduce-101", owner: "cloudera", create: true

  # 7180 (Cloudera Manager web UI)
  config.vm.network "forwarded_port", guest: 7180, host: 7180
 
  # 8020, 50010, 50020, 50070, 50075 (HDFS NameNode and DataNode)
  config.vm.network "forwarded_port", guest: 8020, host: 8020
  config.vm.network "forwarded_port", guest: 50010, host: 50010
  config.vm.network "forwarded_port", guest: 50020, host: 50020
  config.vm.network "forwarded_port", guest: 50070, host: 50070
  config.vm.network "forwarded_port", guest: 50075, host: 50075
 
  # 8021 (MapReduce JobTracker)
  config.vm.network "forwarded_port", guest: 8021, host: 8021
 
  # 8888 (Hue web UI)
  config.vm.network "forwarded_port", guest: 8888, host: 8888
 
  # 9083 (Hive/HCatalog metastore)
  config.vm.network "forwarded_port", guest: 9083, host: 9083
 
  # 41415 (Flume agent)
  config.vm.network "forwarded_port", guest: 41415, host: 41415
 
  # 11000 (Oozie server)
  config.vm.network "forwarded_port", guest: 11000, host: 11000
 
  # 21050 (Impala JDBC port)
  config.vm.network "forwarded_port", guest: 21050, host: 21050

  # VM customizations for virtualbox
  config.vm.provider "virtualbox" do |v|

    # Enables the GUI
    v.gui = true

    # Increases memory/cpu since this VM is process intensive
    v.memory = 2048
    v.cpus = 2

    # Enables shared clipboard
    v.customize ['modifyvm', :id, '--clipboard', 'bidirectional']

  end

  # Builds out the eclipse files for both hadoop and crunch projects while also preping maven by
  # retrieving all the necessary dependencies to build the project
  config.vm.provision "shell", inline: "su - cloudera -c 'cd /home/cloudera/mapreduce-101/hadoop ; mvn clean eclipse:eclipse install'"
  config.vm.provision "shell", inline: "su - cloudera -c 'cd /home/cloudera/mapreduce-101/crunch ; mvn clean eclipse:eclipse install'"
  
end

