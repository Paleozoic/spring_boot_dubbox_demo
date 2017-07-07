/**
 * Copyright 2006-2015 handu.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.handu.open.dubbo.monitor;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RegistryContainer
 *
 * @author Jinkai.Ma
 */
@Service
public class RegistryContainer {

    private final Set<String> applications = new ConcurrentHashSet<String>();

    private final Map<String, Set<String>> providerServiceApplications = new ConcurrentHashMap<String, Set<String>>();

    private final Map<String, Set<String>> providerApplicationServices = new ConcurrentHashMap<String, Set<String>>();

    private final Map<String, Set<String>> consumerServiceApplications = new ConcurrentHashMap<String, Set<String>>();

    private final Map<String, Set<String>> consumerApplicationServices = new ConcurrentHashMap<String, Set<String>>();

    private final Set<String> services = new ConcurrentHashSet<String>();

    private final Map<String, List<URL>> serviceProviders = new ConcurrentHashMap<String, List<URL>>();

    private final Map<String, List<URL>> serviceConsumers = new ConcurrentHashMap<String, List<URL>>();

    @Reference
    private RegistryService registry;

    public RegistryService getRegistry() {
        return registry;
    }

    public Set<String> getApplications() {
        return Collections.unmodifiableSet(applications);
    }

    public Set<String> getDependencies(String application, boolean reverse) {
        if (reverse) {
            Set<String> dependencies = new HashSet<String>();
            Set<String> services = providerApplicationServices.get(application);
            if (services != null && services.size() > 0) {
                for (String service : services) {
                    Set<String> applications = consumerServiceApplications.get(service);
                    if (applications != null && applications.size() > 0) {
                        dependencies.addAll(applications);
                    }
                }
            }
            return dependencies;
        } else {
            Set<String> dependencies = new HashSet<String>();
            Set<String> services = consumerApplicationServices.get(application);
            if (services != null && services.size() > 0) {
                for (String service : services) {
                    Set<String> applications = providerServiceApplications.get(service);
                    if (applications != null && applications.size() > 0) {
                        dependencies.addAll(applications);
                    }
                }
            }
            return dependencies;
        }
    }

    public Set<String> getServices() {
        return Collections.unmodifiableSet(services);
    }

    public Map<String, List<URL>> getServiceProviders() {
        return Collections.unmodifiableMap(serviceProviders);
    }

    public List<URL> getProvidersByService(String service) {
        List<URL> urls = serviceProviders.get(service);
        return urls == null ? null : Collections.unmodifiableList(urls);
    }

    public List<URL> getProvidersByHost(String host) {
        List<URL> urls = new ArrayList<URL>();
        if (host != null && host.length() > 0) {
            for (List<URL> providers : serviceProviders.values()) {
                for (URL url : providers) {
                    if (host.equals(url.getHost())) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    public List<URL> getProvidersByApplication(String application) {
        List<URL> urls = new ArrayList<URL>();
        if (application != null && application.length() > 0) {
            for (List<URL> providers : serviceProviders.values()) {
                for (URL url : providers) {
                    if (application.equals(url.getParameter(Constants.APPLICATION_KEY))) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    public Set<String> getHosts() {
        Set<String> addresses = new HashSet<String>();
        for (List<URL> providers : serviceProviders.values()) {
            for (URL url : providers) {
                addresses.add(url.getHost());
            }
        }
        for (List<URL> providers : serviceConsumers.values()) {
            for (URL url : providers) {
                addresses.add(url.getHost());
            }
        }
        return addresses;
    }

    public Map<String, List<URL>> getServiceConsumers() {
        return Collections.unmodifiableMap(serviceConsumers);
    }

    public List<URL> getConsumersByService(String service) {
        List<URL> urls = serviceConsumers.get(service);
        return urls == null ? null : Collections.unmodifiableList(urls);
    }

    public List<URL> getConsumersByHost(String host) {
        List<URL> urls = new ArrayList<URL>();
        if (host != null && host.length() > 0) {
            for (List<URL> consumers : serviceConsumers.values()) {
                for (URL url : consumers) {
                    if (host.equals(url.getHost())) {
                        urls.add(url);
                    }
                }
            }
        }
        return Collections.unmodifiableList(urls);
    }

    public List<URL> getConsumersByApplication(String application) {
        List<URL> urls = new ArrayList<URL>();
        if (application != null && application.length() > 0) {
            for (List<URL> consumers : serviceConsumers.values()) {
                for (URL url : consumers) {
                    if (application.equals(url.getParameter(Constants.APPLICATION_KEY))) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    @PostConstruct
    public void start() {
        URL subscribeUrl = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "",
                Constants.INTERFACE_KEY, Constants.ANY_VALUE,
                Constants.GROUP_KEY, Constants.ANY_VALUE,
                Constants.VERSION_KEY, Constants.ANY_VALUE,
                Constants.CLASSIFIER_KEY, Constants.ANY_VALUE,
                Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY + ","
                + Constants.CONSUMERS_CATEGORY,
                Constants.CHECK_KEY, String.valueOf(false));
        registry.subscribe(subscribeUrl, new NotifyListener() {
            public void notify(List<URL> urls) {
                if (urls == null || urls.size() == 0) {
                    return;
                }
                Map<String, List<URL>> proivderMap = new HashMap<String, List<URL>>();
                Map<String, List<URL>> consumerMap = new HashMap<String, List<URL>>();
                for (URL url : urls) {
                    String application = url.getParameter(Constants.APPLICATION_KEY);
                    if (application != null && application.length() > 0) {
                        applications.add(application);
                    }
                    String service = url.getServiceInterface();
                    services.add(service);
                    String category = url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
                    if (Constants.PROVIDERS_CATEGORY.equals(category)) {
                        if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol())) {
                            serviceProviders.remove(service);
                        } else {
                            List<URL> list = proivderMap.get(service);
                            if (list == null) {
                                list = new ArrayList<URL>();
                                proivderMap.put(service, list);
                            }
                            list.add(url);
                            if (application != null && application.length() > 0) {
                                Set<String> serviceApplications = providerServiceApplications.get(service);
                                if (serviceApplications == null) {
                                    providerServiceApplications.put(service, new ConcurrentHashSet<String>());
                                    serviceApplications = providerServiceApplications.get(service);
                                }
                                serviceApplications.add(application);

                                Set<String> applicationServices = providerApplicationServices.get(application);
                                if (applicationServices == null) {
                                    providerApplicationServices.put(application, new ConcurrentHashSet<String>());
                                    applicationServices = providerApplicationServices.get(application);
                                }
                                applicationServices.add(service);
                            }
                        }
                    } else if (Constants.CONSUMERS_CATEGORY.equals(category)) {
                        if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol())) {
                            serviceConsumers.remove(service);
                        } else {
                            List<URL> list = consumerMap.get(service);
                            if (list == null) {
                                list = new ArrayList<URL>();
                                consumerMap.put(service, list);
                            }
                            list.add(url);
                            if (application != null && application.length() > 0) {
                                Set<String> serviceApplications = consumerServiceApplications.get(service);
                                if (serviceApplications == null) {
                                    consumerServiceApplications.put(service, new ConcurrentHashSet<String>());
                                    serviceApplications = consumerServiceApplications.get(service);
                                }
                                serviceApplications.add(application);

                                Set<String> applicationServices = consumerApplicationServices.get(application);
                                if (applicationServices == null) {
                                    consumerApplicationServices.put(application, new ConcurrentHashSet<String>());
                                    applicationServices = consumerApplicationServices.get(application);
                                }
                                applicationServices.add(service);
                            }

                        }
                    }
                }
                if (proivderMap != null && proivderMap.size() > 0) {
                    serviceProviders.putAll(proivderMap);
                }
                if (consumerMap != null && consumerMap.size() > 0) {
                    serviceConsumers.putAll(consumerMap);
                }
            }
        });
    }

    @PreDestroy
    public void stop() {
    }

}