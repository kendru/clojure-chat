# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.define :infr do |infr|
    infr.vm.box = "ubuntu/trusty64"
    infr.vm.hostname = "infr"
    infr.vm.network :private_network, ip: "10.0.15.10"
    infr.vm.provider "virtualbox" do |vb|
      vb.memory = "512"
    end
  end

  config.vm.define :web do |web|
    web.vm.box = "ubuntu/trusty64"
    web.vm.hostname = "web"
    web.vm.network :private_network, ip: "10.0.15.11"
    web.vm.provider "virtualbox" do |vb|
      vb.memory = "256"
    end
  end

  config.vm.define :app do |app|
    app.vm.box = "ubuntu/trusty64"
    app.vm.hostname = "app"
    app.vm.network :private_network, ip: "10.0.15.12"
    app.vm.provider "virtualbox" do |vb|
      vb.memory = "256"
    end
  end

  config.vm.provision "ansible" do |ansible|
    ansible.playbook = "config/provision.yml"

    ansible.groups = {
      "web" => ["web"],
      "application" => ["app"],
      "database" => ["infr"],
      "broker" => ["infr"],
      "common:children" => ["web", "application", "database", "broker"]
    }
  end
end
