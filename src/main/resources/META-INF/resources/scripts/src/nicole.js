/**
 * @author Sven Koelpin
 **/
(function (w, d) {
    "use strict";

    w.Nicole = (function () {
        var registeredComponentModules = {};
        var moduleInstances = {};
        var jsfEventRegistered = false;

        /**
         * called to register a component
         * @param name name of the component. must be unique
         * @param fn a function that contains the components js-logic
         */
        var module = function (name, fn) {
            if (typeof name !== "string" || typeof fn !== "function") {
                throw new Error("module-name and module-function must be defined!");
            }
            if (!registeredComponentModules[name]) {
                registeredComponentModules[name] = fn;
            } else {
                throw new Error("module " + name + " declared multiple times");
            }
        };

        /**
         * starts the framework by parsing the dom for data-components
         */
        var start = function (root) {
            if (!root) {
                root = d;
            }
            var declaredComponents = root.querySelectorAll("input[data-modulename]");
            //use a normal for-loop instead of foreach because its faster
            for (var i = 0, length = declaredComponents.length; i < length; i++) {
                var id = declaredComponents[i].getAttribute("id");
                if (id.indexOf("nicole", id.length - 6) !== -1) {
                    bootstrapComponent(declaredComponents[i]);
                }
            }
            if (w.jsf && !jsfEventRegistered) {
                w.jsf.ajax.addOnEvent(function (data) {
                    if (data.status === "success") {
                        var updates = data.responseXML.getElementsByTagName("update");
                        for (var i = 0, length = updates.length; i < length; i++) {
                            //bootstrap components after an ajax request
                            start(d.getElementById(updates[i].getAttribute("id")));
                        }
                    }
                });
                jsfEventRegistered = true;
            }
        };


        /**
         * private method that bootstraps a component which was found in the dom.
         * @param declaredComponent
         */
        var bootstrapComponent = function (declaredComponent) {
            //get the module
            var moduleName = getDataAttribute(declaredComponent, "modulename");
            if (!moduleInstances[moduleName]) {
                moduleInstances[moduleName] = 0;
            }
            moduleInstances[moduleName] += 1;
            var instanceId = getDataAttribute(declaredComponent, "instanceid");
            var compModule = registeredComponentModules[moduleName];
            if (!compModule) {
                throw new Error("There is no js-module for the declared module '" + moduleName + "'");
            }
            //get attributes declared in data-* attributes (except for data-modulename)
            var parameters = {};
            if (declaredComponent.dataset) {
                for (var dataKey in declaredComponent.dataset) {
                    if (declaredComponent.dataset.hasOwnProperty(dataKey) && dataKey !== "modulename") {
                        parameters[dataKey] = declaredComponent.dataset[dataKey];
                    }
                }
            } else {
                //<ie 11
                for (var entry in declaredComponent.attributes) {
                    if (declaredComponent.attributes.hasOwnProperty(entry) &&
                        declaredComponent.attributes[entry].name !== "data-modulename") {
                        parameters[declaredComponent.attributes[entry].name.substr(5).toLowerCase()] =
                            declaredComponent.attributes[entry].value;
                    }
                }
            }
            var baseModule = NicoleBaseModule.create(parameters);
            //generate name if no instanceid was set
            baseModule.instanceId = instanceId || (moduleName + "_" + moduleInstances[moduleName]);
            compModule.call(baseModule);
        };


        var getDataAttribute = function (obj, attr) {
            if (obj.dataset) {
                return obj.dataset[attr];
            } else {
                //< ie 11
                return obj.getAttribute("data-" + attr);
            }
        };

        return {
            start: start,
            module: module
        };
    }());

    w.Nicole.events = {};

    var NicoleBaseModule = (function () {
        var $elm = function (id) {
            if (w.$) {
                return w.$("#" + (clientId.call(this, id)).replace(/:/g, "\\:"));
            }
            throw new Error("jquery required");
        };

        var escapeId = function (id) {
            return id.replace(/:/g, "\\:");
        };

        var elm = function (id) {
            return d.getElementById(clientId.call(this, id));
        };

        var parameter = function (param, type) {
            if (typeof param !== "undefined" && typeof param === "string") {
                param = param.toLowerCase();
                if (typeof this._params[param] !== "undefined") {
                    if (type) {
                        if (type === "int") {
                            return parseInt(this._params[param], 10);
                        }
                        if (type === "float") {
                            return parseFloat(this._params[param]);
                        }
                        if (type === "bool") {
                            return this._params[param] === "true";
                        }
                    }
                    return this._params[param];
                }
            }
            throw new Error("param " + param + " is not defined");
        };

        var parameterHasValue = function (param) {
            return this.parameter(param).length > 0;
        };

        var ajax = function (elmId, opts) {
            var execute = createIdString.call(this, opts.execute);
            var render = createIdString.call(this, opts.render);

            w.jsf.ajax.request(this.elm(elmId), null, {
                execute: execute,
                render: render,
                onevent: function (data) {
                    if (data.status === "begin" && (opts.begin && typeof opts.begin === "function")) {
                        opts.begin.call(null, data);
                    }
                    if (data.status === "complete" && (opts.complete && typeof opts.complete === "function")) {
                        opts.complete.call(null, data);
                    }
                    if (data.status === "success" && (opts.success && typeof opts.success === "function")) {
                        opts.success.call(null, data);
                    }
                }
            });
        };

        var createIdString = function (idArray) {
            var result = "@none";
            if (idArray && idArray instanceof Array) {
                result = "";
                idArray.forEach(function (id) {
                    if (typeof id === "string") {
                        result = result + " " + this.clientId(id);
                    }
                }.bind(this));
            } else if (typeof idArray === "string") {
                result = idArray;
            }
            return result;
        };


        var clientId = function (id) {
            if (this._clientid.length === 0) {
                return id;
            }
            return this._clientid + ":" + id;
        };

        var emit = function (name, evt) {
            if (w.Nicole.events[name]) {
                if (!evt) {
                    evt = {};
                }
                evt.src = this.instanceId;
                w.Nicole.events[name].forEach(function (evtObj) {
                    if (!evtObj.src || evtObj.src === evt.src) {
                        evtObj.callBack(evt);
                    }
                });
            }
        };

        var subscribe = function (name, callback, src) {
            if (!w.Nicole.events[name]) {
                w.Nicole.events[name] = [];
            }
            w.Nicole.events[name].push({callBack: callback, src: src});
        };

        return {
            create: function (params) {
                var _clientId = params.clientid;
                if (!_clientId || _clientId.length < 1) {
                    _clientId = "";
                }
                return  {
                    _clientid: _clientId,
                    _params: params,
                    elm: elm,
                    $elm: $elm,
                    clientId: clientId,
                    ajax: ajax,
                    parameter: parameter,
                    parameterHasValue: parameterHasValue,
                    escapeId: escapeId,
                    subscribe: subscribe,
                    emit: emit
                };
            }
        };
    }());

    if (!w.$) {
        d.addEventListener("DOMContentLoaded", function () {
            w.Nicole.start();
        });
    } else {
        w.$(d).ready(function () {
            w.Nicole.start();
        });
    }
}(window, window.document));