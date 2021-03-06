/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.controller.operations.global;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ACCESS_CONTROL;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.operations.global.GlobalOperationAttributes.RECURSIVE;
import static org.jboss.as.controller.operations.global.GlobalOperationAttributes.RECURSIVE_DEPTH;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ProcessType;
import org.jboss.as.controller.UnauthorizedException;
import org.jboss.as.controller.logging.ControllerLogger;
import org.jboss.as.controller.registry.AliasEntry;
import org.jboss.as.controller.registry.ImmutableManagementResourceRegistration;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * Global {@code OperationHandler}s.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GlobalOperationHandlers {


    private static final Set<String> GLOBAL_READ_OPERATION_NAMES;
    static {
        Set<String> set = new HashSet<String>();
        set.add(ReadResourceHandler.DEFINITION.getName());
        set.add(ReadAttributeHandler.DEFINITION.getName());
        set.add(ReadResourceDescriptionHandler.DEFINITION.getName());
        set.add(ReadChildrenNamesHandler.DEFINITION.getName());
        set.add(ReadChildrenTypesHandler.DEFINITION.getName());
        set.add(ReadChildrenResourcesHandler.DEFINITION.getName());
        set.add(ReadOperationNamesHandler.DEFINITION.getName());
        set.add(ReadOperationDescriptionHandler.DEFINITION.getName());
        set.add(ReadResourceDescriptionHandler.CheckResourceAccessHandler.DEFINITION.getName());
        set.add(MapOperations.MAP_PUT_DEFINITION.getName());
        set.add(MapOperations.MAP_GET_DEFINITION.getName());
        set.add(MapOperations.MAP_CLEAR_DEFINITION.getName());
        set.add(MapOperations.MAP_REMOVE_DEFINITION.getName());
        set.add(ListOperations.LIST_ADD_DEFINITION.getName());
        set.add(ReadAttributeGroupNamesHandler.DEFINITION.getName());
        set.add(ReadAttributeGroupHandler.DEFINITION.getName());

        GLOBAL_READ_OPERATION_NAMES = Collections.unmodifiableSet(set);
    }

    public static boolean isGlobalReadOperation(String operationName) {
        return GLOBAL_READ_OPERATION_NAMES.contains(operationName);
    }

    public static void registerGlobalOperations(ManagementResourceRegistration root, ProcessType processType) {
        if( processType == ProcessType.HOST_CONTROLLER) {
            root.registerOperationHandler(org.jboss.as.controller.operations.global.ReadResourceHandler.DEFINITION,
                    org.jboss.as.controller.operations.global.ReadResourceHandler.INSTANCE, true);
            root.registerOperationHandler(org.jboss.as.controller.operations.global.ReadAttributeHandler.DEFINITION,
                    org.jboss.as.controller.operations.global.ReadAttributeHandler.INSTANCE, true);
            root.registerOperationHandler(ReadAttributeGroupHandler.DEFINITION, ReadAttributeGroupHandler.INSTANCE, true);
        }else{
            root.registerOperationHandler(org.jboss.as.controller.operations.global.ReadResourceHandler.RESOLVE_DEFINITION,
                    org.jboss.as.controller.operations.global.ReadResourceHandler.RESOLVE_INSTANCE, true);
            root.registerOperationHandler(org.jboss.as.controller.operations.global.ReadAttributeHandler.RESOLVE_DEFINITION,
                    org.jboss.as.controller.operations.global.ReadAttributeHandler.RESOLVE_INSTANCE, true);
            root.registerOperationHandler(ReadAttributeGroupHandler.RESOLVE_DEFINITION, ReadAttributeGroupHandler.RESOLVE_INSTANCE, true);
        }

        root.registerOperationHandler(ReadResourceDescriptionHandler.DEFINITION, ReadResourceDescriptionHandler.INSTANCE, true);
        root.registerOperationHandler(ReadAttributeGroupNamesHandler.DEFINITION, ReadAttributeGroupNamesHandler.INSTANCE, true);
        root.registerOperationHandler(ReadChildrenNamesHandler.DEFINITION, ReadChildrenNamesHandler.INSTANCE, true);
        root.registerOperationHandler(ReadChildrenTypesHandler.DEFINITION, ReadChildrenTypesHandler.INSTANCE, true);
        root.registerOperationHandler(ReadChildrenResourcesHandler.DEFINITION, ReadChildrenResourcesHandler.INSTANCE, true);
        root.registerOperationHandler(ReadOperationNamesHandler.DEFINITION, ReadOperationNamesHandler.INSTANCE, true);
        root.registerOperationHandler(ReadOperationDescriptionHandler.DEFINITION, ReadOperationDescriptionHandler.INSTANCE, true);
        //map operations
        root.registerOperationHandler(MapOperations.MAP_PUT_DEFINITION, MapOperations.MAP_PUT_HANDLER, true);
        root.registerOperationHandler(MapOperations.MAP_GET_DEFINITION, MapOperations.MAP_GET_HANDLER, true);
        root.registerOperationHandler(MapOperations.MAP_REMOVE_DEFINITION, MapOperations.MAP_REMOVE_HANDLER, true);
        root.registerOperationHandler(MapOperations.MAP_CLEAR_DEFINITION, MapOperations.MAP_CLEAR_HANDLER, true);
        //list operations
        root.registerOperationHandler(ListOperations.LIST_ADD_DEFINITION, ListOperations.LIST_ADD_HANDLER, true);
        root.registerOperationHandler(ListOperations.LIST_REMOVE_DEFINITION, ListOperations.LIST_REMOVE_HANDLER, true);
        root.registerOperationHandler(ListOperations.LIST_GET_DEFINITION, ListOperations.LIST_GET_HANDLER, true);
        root.registerOperationHandler(ListOperations.LIST_CLEAR_DEFINITION, ListOperations.LIST_CLEAR_HANDLER, true);

        root.registerOperationHandler(ReadResourceDescriptionHandler.CheckResourceAccessHandler.DEFINITION, new OperationStepHandler() {
            @Override
            public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                //Just use an empty operation handler here, this is a private operation and people who want to call it need to instantiate the step handler
                throw new OperationFailedException("This should never be called");
            }
        }, true);
        root.registerOperationHandler(ReadResourceDescriptionHandler.CheckResourceAccessHandler.DEFAULT_DEFINITION, new OperationStepHandler() {
            @Override
            public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                //Just use an empty operation handler here, this is a private operation and people who want to call it need to instantiate the step handler
                throw new OperationFailedException("This should never be called");
            }
        }, true);

        if (processType != ProcessType.DOMAIN_SERVER) {
            root.registerOperationHandler(org.jboss.as.controller.operations.global.WriteAttributeHandler.DEFINITION,
                                          org.jboss.as.controller.operations.global.WriteAttributeHandler.INSTANCE, true);
            root.registerOperationHandler(org.jboss.as.controller.operations.global.UndefineAttributeHandler.DEFINITION,
                                          org.jboss.as.controller.operations.global.UndefineAttributeHandler.INSTANCE, true);
        }
    }

    public static final String CHECK_DEFAULT_RESOURCE_ACCESS = "check-default-resource-access";

    public static final String CHECK_RESOURCE_ACCESS = "check-resource-access";

    private GlobalOperationHandlers() {
        //
    }

    public abstract static class AbstractMultiTargetHandler implements OperationStepHandler {

        public static final ModelNode FAKE_OPERATION;

        static {
            final ModelNode resolve = new ModelNode();
            resolve.get(OP).set("resolve");
            resolve.get(OP_ADDR).setEmptyList();
            resolve.protect();
            FAKE_OPERATION = resolve;
        }

        private final FilteredData filteredData;

        protected AbstractMultiTargetHandler() {
            this(null);
        }

        protected AbstractMultiTargetHandler(FilteredData filteredData) {
            this.filteredData = filteredData;
        }

        protected FilteredData getFilteredData() {
            return filteredData;
        }


        @Override
        public void execute(final OperationContext context, final ModelNode operation) throws OperationFailedException {
            final PathAddress address = context.getCurrentAddress();

            // In case if it's a multiTarget operation, resolve the address first
            // This only works for model resources, which can be resolved into a concrete addresses
            if (address.isMultiTarget()) {
                final FilteredData localFilteredData = filteredData == null ? new FilteredData(PathAddress.EMPTY_ADDRESS) : filteredData;
                // The final result should be a list of executed operations
                final ModelNode result = context.getResult().setEmptyList();
                // Trick the context to give us the model-root
                context.addStep(new ModelNode(), FAKE_OPERATION.clone(), new ModelAddressResolver(operation, result, localFilteredData,
                        new OperationStepHandler() {
                    @Override
                    public void execute(final OperationContext context, final ModelNode operation) throws OperationFailedException {
                        doExecute(context, operation, localFilteredData);
                    }
                }), OperationContext.Stage.MODEL, true);
                context.completeStep(new OperationContext.ResultHandler() {
                    @Override
                    public void handleResult(OperationContext.ResultAction resultAction, OperationContext context, ModelNode operation) {
                        if (resultAction == OperationContext.ResultAction.KEEP && localFilteredData.hasFilteredData()) {
                            // Report on filtering
                            context.getResponseHeaders().get(ACCESS_CONTROL).set(localFilteredData.toModelNode());
                        }
                    }
                });
            } else {
                doExecute(context, operation, filteredData);
            }
        }

        /**
         * Execute the actual operation if it is not addressed to multiple targets.
         *
         *
         * @param context      the operation context
         * @param operation    the original operation
         * @param filteredData tracking object for filtered data
         * @throws OperationFailedException
         */
        abstract void doExecute(OperationContext context, ModelNode operation, FilteredData filteredData) throws OperationFailedException;
    }

    public static final class ModelAddressResolver implements OperationStepHandler {

        private final ModelNode operation;
        private final ModelNode result;
        private final FilteredData filteredData;
        private final OperationStepHandler handler; // handler bypassing further wildcard resolution

        public ModelAddressResolver(final ModelNode operation, final ModelNode result,
                                    final FilteredData filteredData,
                                    final OperationStepHandler delegate) {
            this.operation = operation;
            this.result = result;
            this.handler = delegate;
            this.filteredData = filteredData;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute(final OperationContext context, final ModelNode ignored) throws OperationFailedException {
            final PathAddress address = PathAddress.pathAddress(operation.require(OP_ADDR));
            execute(address, PathAddress.EMPTY_ADDRESS, context);
            context.completeStep(new OperationContext.ResultHandler() {
                @Override
                public void handleResult(OperationContext.ResultAction resultAction, OperationContext context, ModelNode operation) {

                    if (result.getType() == ModelType.LIST) {
                        boolean replace = false;
                        ModelNode replacement = new ModelNode().setEmptyList();
                        for (ModelNode item : result.asList()) {
                            if (item.isDefined() && item.hasDefined(OP_ADDR)) {
                                replacement.add(item);
                            } else {
                                replace = true;
                            }
                        }
                        if (replace) {
                            result.set(replacement);
                        }
                    }
                }
            });
        }

        private void safeExecute(final PathAddress address, final PathAddress base, final OperationContext context) {
            try {
                execute(address, base, context);
            } catch (UnauthorizedException e) {
                // equivalent to the resource not existing
                // Just report the failure to the filter and complete normally
                filteredData.addReadRestrictedResource(base);
            } catch (Resource.NoSuchResourceException e) {
                // Just report the failure to the filter and complete normally
                filteredData.addAccessRestrictedResource(base);
            }
        }

        private void execute(final PathAddress address, final PathAddress base, final OperationContext context) {
            final Resource resource = context.readResource(base, false);
            final PathAddress current = address.subAddress(base.size());
            final Iterator<PathElement> iterator = current.iterator();
            if (iterator.hasNext()) {
                final PathElement element = iterator.next();
                if (element.isMultiTarget()) {
                    final String childType = element.getKey().equals("*") ? null : element.getKey();
                    final ImmutableManagementResourceRegistration registration = context.getResourceRegistration().getSubModel(base);
                    if (registration.isRemote() || registration.isRuntimeOnly()) {
                        // At least for proxies it should use the proxy operation handler
                        throw new IllegalStateException();
                    }
                    final Map<String, Set<String>> resolved = getChildAddresses(context, address, registration, resource, childType);
                    for (Map.Entry<String, Set<String>> entry : resolved.entrySet()) {
                        final String key = entry.getKey();
                        final Set<String> children = entry.getValue();
                        if (children.isEmpty()) {
                            continue;
                        }
                        if (element.isWildcard()) {
                            for (final String child : children) {
                                // Double check if the child actually exists
                                if (resource.hasChild(PathElement.pathElement(key, child))) {
                                    safeExecute(address, base.append(PathElement.pathElement(key, child)), context);
                                }
                            }
                        } else {
                            for (final String segment : element.getSegments()) {
                                if (children.contains(segment)) {
                                    // Double check if the child actually exists
                                    if (resource.hasChild(PathElement.pathElement(key, segment))) {
                                        safeExecute(address, base.append(PathElement.pathElement(key, segment)), context);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Double check if the child actually exists
                    if (resource.hasChild(element)) {
                        safeExecute(address, base.append(element), context);
                    }
                }
            } else {
                //final String operationName = operation.require(OP).asString();
                final ModelNode newOp = operation.clone();
                newOp.get(OP_ADDR).set(base.toModelNode());

                final ModelNode resultItem = this.result.add();
                final ModelNode resultAddress = resultItem.get(OP_ADDR);

                final OperationStepHandler wrapper = new OperationStepHandler() {
                    @Override
                    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                        try {
                            handler.execute(context, operation);
                            resultAddress.set(base.toModelNode());
                        } catch (Resource.NoSuchResourceException e) {
                            // just discard the result to avoid leaking the inaccessible address
                        }
                    }
                };
                context.addStep(resultItem, newOp, wrapper, OperationContext.Stage.MODEL, true);
            }
        }

    }

    static class RegistrationAddressResolver implements OperationStepHandler {

        private final ModelNode operation;
        private final ModelNode result;
        private final OperationStepHandler handler; // handler bypassing further wildcard resolution

        RegistrationAddressResolver(final ModelNode operation, final ModelNode result, final OperationStepHandler delegate) {
            this.operation = operation;
            this.result = result;
            this.handler = delegate;
        }

        @Override
        public void execute(final OperationContext context, final ModelNode ignored) throws OperationFailedException {
            final PathAddress address = PathAddress.pathAddress(operation.require(OP_ADDR));
            execute(address, PathAddress.EMPTY_ADDRESS, context);
        }

        void execute(final PathAddress address, PathAddress base, final OperationContext context) {
            final PathAddress current = address.subAddress(base.size());
            final Iterator<PathElement> iterator = current.iterator();
            if (iterator.hasNext()) {
                final PathElement element = iterator.next();
                if (element.isMultiTarget()) {
                    final Set<PathElement> children = context.getResourceRegistration().getChildAddresses(base);
                    if (children == null || children.isEmpty()) {
                        return;
                    }
                    final String childType = element.getKey().equals("*") ? null : element.getKey();
                    for (final PathElement path : children) {
                        if (childType != null && !childType.equals(path.getKey())) {
                            continue;
                        }
                        execute(address, base.append(path), context);
                    }
                } else {
                    execute(address, base.append(element), context);
                }
            } else {
                //final String operationName = operation.require(OP).asString();
                final ModelNode newOp = operation.clone();
                newOp.get(OP_ADDR).set(base.toModelNode());

                final ModelNode result = this.result.add();
                result.get(OP_ADDR).set(base.toModelNode());
                context.addStep(result, newOp, handler, OperationContext.Stage.MODEL, true);
            }
        }
    }

    /**
     * Gets the addresses of the child resources under the given resource.
     *
     * @param context        the operation context
     * @param registry       registry entry representing the resource
     * @param resource       the current resource
     * @param validChildType a single child type to which the results should be limited. If {@code null} the result
     *                       should include all child types
     * @return map where the keys are the child types and the values are a set of child names associated with a type
     */
    static Map<String, Set<String>> getChildAddresses(final OperationContext context, final PathAddress addr, final ImmutableManagementResourceRegistration registry, Resource resource, final String validChildType) {

        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        Set<PathElement> elements = registry.getChildAddresses(PathAddress.EMPTY_ADDRESS);
        for (PathElement element : elements) {
            String childType = element.getKey();
            if (validChildType != null && !validChildType.equals(childType)) {
                continue;
            }
            final ImmutableManagementResourceRegistration childRegistration = registry.getSubModel(PathAddress.pathAddress(element));
            final AliasEntry aliasEntry = childRegistration.getAliasEntry();

            Set<String> set = result.get(childType);
            if (set == null) {
                set = new LinkedHashSet<String>();
                result.put(childType, set);
            }

            if (aliasEntry == null) {
                if (resource != null && resource.hasChildren(childType)) {
                    Set<String> childNames = resource.getChildrenNames(childType);
                    if (element.isWildcard()) {
                        set.addAll(childNames);
                    } else if (childNames.contains(element.getValue())) {
                        set.add(element.getValue());
                    }
                }
            } else {
                //PathAddress target = aliasEntry.getTargetAddress();
                PathAddress target = aliasEntry.convertToTargetAddress(addr.append(element));
                PathAddress targetParent = target.subAddress(0, target.size() - 1);
                Resource parentResource = context.readResourceFromRoot(targetParent, false);
                if (parentResource != null && parentResource.hasChildren(target.getLastElement().getKey())) {
                    set.add(element.getValue());
                }
            }
            if (!element.isWildcard()) {
                ImmutableManagementResourceRegistration childReg = registry.getSubModel(PathAddress.pathAddress(element));
                if (childReg != null && childReg.isRuntimeOnly()) {
                    set.add(element.getValue());
                }
            }
        }

        // WFLY-3306 Ensure we have an entry for any valid child type
        for (String type : registry.getChildNames(PathAddress.EMPTY_ADDRESS)) {
            if ((validChildType == null || validChildType.equals(type))
                && !result.containsKey(type)) {
                result.put(type, Collections.<String>emptySet());
            }
        }

        return result;
    }

    static Locale getLocale(OperationContext context, final ModelNode operation) throws OperationFailedException {
        if (!operation.hasDefined(GlobalOperationAttributes.LOCALE.getName())) {
            return null;
        }
        String unparsed = normalizeLocale(operation.get(GlobalOperationAttributes.LOCALE.getName()).asString());
        try {
            return LocaleResolver.resolveLocale(unparsed);
        } catch (IllegalArgumentException e) {
            reportInvalidLocaleFormat(context, e.getMessage());
            return null;
        }
    }

    static boolean getRecursive(OperationContext context, ModelNode op) throws OperationFailedException
    {
        // -1 means UNDEFINED
        ModelNode recursiveNode = RECURSIVE.resolveModelAttribute(context, op);
        final int recursiveValue = recursiveNode.isDefined() ? (recursiveNode.asBoolean() ? 1 : 0) : -1;
        final int recursiveDepthValue = RECURSIVE_DEPTH.resolveModelAttribute(context, op).asInt(-1);
        // WFCORE-76: We are recursing in this round IFF:
        //  Recursive is explicitly specified as TRUE and recursiveDepth is UNDEFINED
        //  Recursive is either TRUE or UNDEFINED and recursiveDepth is >0
        return recursiveValue > 0 && recursiveDepthValue == -1 || //
                recursiveValue != 0 && recursiveDepthValue > 0;
    }

    static void setNextRecursive(OperationContext context, ModelNode op, ModelNode nextOp) throws OperationFailedException
    {
        // -1 means UNDEFINED
        final int recursiveDepthValue = RECURSIVE_DEPTH.resolveModelAttribute(context, op).asInt(-1);
        // WFCORE-76: We are recursing in the next step IFF:
        //  Recursive is explicitly specified as TRUE and recursiveDepth is UNDEFINED; or
        //  Recursive is either TRUE or UNDEFINED and (recursiveDepth - 1) is >0

        // Recursive value carries through unchanged
        nextOp.get(RECURSIVE.getName()).set(op.get(RECURSIVE.getName()));
        switch(recursiveDepthValue) {
        case -1:
            // Undefined stays undefined
            nextOp.get(RECURSIVE_DEPTH.getName()).set(op.get(RECURSIVE_DEPTH.getName()));
            break;
        case 0:
            nextOp.get(RECURSIVE_DEPTH.getName()).set(recursiveDepthValue);
            break;
        default:
            nextOp.get(RECURSIVE_DEPTH.getName()).set(recursiveDepthValue - 1);
            break;
        }
    }

    private static String normalizeLocale(String toNormalize) {
        return ("zh_Hans".equalsIgnoreCase(toNormalize) || "zh-Hans".equalsIgnoreCase(toNormalize)) ? "zh_CN" : toNormalize;
    }

    private static void reportInvalidLocaleFormat(OperationContext context, String format) {
        String msg = ControllerLogger.ROOT_LOGGER.invalidLocaleString(format);
        ControllerLogger.MGMT_OP_LOGGER.debug(msg);
        // TODO report the problem to client via out-of-band message.
        // Enable this in 7.2 or later when there is time to test
        //context.report(MessageSeverity.WARN, msg);
    }


}
