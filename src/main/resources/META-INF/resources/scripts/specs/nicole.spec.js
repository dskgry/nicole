/**
 * @author Sven Koelpin
 */
(function (w) {

    var mockSelectAll = function () {
        return [
            {
                dataset: {
                    modulename: "SomeModule"
                },
                getAttribute: function () {
                    return "nicole";
                }
            }
        ];
    };


    describe("Nicole", function () {
        it("has Nicole defined", function () {
            expect(Nicole).not.toBeNull();
        });
        it("bootstrap component without module fails", function () {
            w.document.querySelectorAll = mockSelectAll;
            expect(function () {
                Nicole.start(w.document);
            }).toThrow(new Error("There is no js-module for the declared module 'SomeModule'"));
        });
        it("works when no components are defined", function () {
            w.document.querySelectorAll = function () {
                return [];
            };

            expect(function () {
                Nicole.start(w.document);
            }).not.toThrow();
        });
        it("fails when a component does not have a name or function", function () {

            expect(function () {
                Nicole.module();
            }).toThrow(new Error("module-name and module-function must be defined!"));
            expect(function () {
                Nicole.module("s");
            }).toThrow(new Error("module-name and module-function must be defined!"));
            expect(function () {
                Nicole.module(function () {
                });
            }).toThrow(new Error("module-name and module-function must be defined!"));
        });
        it("creates a module when name and function are defined", function () {
            expect(function () {
                Nicole.module("module", function () {
                });
            }).not.toThrow();
        });

        it("bootstraps the defined modules", function () {
            expect(function () {
                Nicole.module("SomeModule", function () {
                });
                Nicole.module("SomeModule1", function () {
                });
                Nicole.module("SomeModule2", function () {
                });
            }).not.toThrow();
            w.document.querySelectorAll = function () {
                return [
                    {
                        dataset: {
                            modulename: "SomeModule"
                        },
                        getAttribute: function () {
                            return "nicole";
                        }
                    } ,
                    {
                        dataset: {
                            modulename: "SomeModule1"
                        },
                        getAttribute: function () {
                            return "nicole";
                        }
                    },
                    {
                        dataset: {
                            modulename: "SomeModule2"
                        },
                        getAttribute: function () {
                            return "nicole";
                        }
                    }
                ];
            };
            expect(function () {
                Nicole.start(w.document);
            }).not.toThrow();
        });


    });
})(window);