package org.nectarframework.base.config;

import java.util.List;
import java.util.Map;

import org.nectarframework.base.service.Service;

public final class Configuration extends BasicConfig {
	private final Map<String, List<Service>> serviceListByNodeGroup;

	public Configuration(Map<String, List<Service>> serviceListByNodeGroup, BasicConfig basicConfig) {
		super(basicConfig);
		this.serviceListByNodeGroup = serviceListByNodeGroup;
	}

	public List<Service> getServiceList(String nodeGroup) {
		return serviceListByNodeGroup.get(nodeGroup);
	}
}
