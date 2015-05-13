module.exports = function (grunt) {
    'use strict';
    var path = "src/main/resources/META-INF/resources/";
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +
            '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
            '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
            '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
            ' Licensed <%= pkg.license %> */\n',
        concat: {
            options: {
                banner: '<%= banner %>',
                stripBanners: true
            },
            dist: {
                files: {
                    "src/main/resources/META-INF/resources/scripts/dist/nicole.js": [path + "scripts/src/**/*.js"]
                }
            }
        },
        uglify: {
            options: {
                banner: '<%= banner %>'
            },
            dist: {
                src: path + "scripts/dist/nicole.js",
                dest: path + "scripts/dist/nicole.min.js"
            }
        },
        jshint: {
            options: {
                globals: {
                    window: true
                },
                node: true,
                curly: true,
                eqeqeq: true,
                immed: true,
                latedef: true,
                newcap: true,
                noarg: true,
                sub: true,
                undef: true,
                unused: true,
                eqnull: true,
                boss: true,
                bitwise: true,
                camelcase: true,
                forin: true,
                quotmark: "double",
                strict: true
            },
            files: ['!Gruntfile.js', path + 'scripts/src/**/*.js']
        },
        jasmine: {
            runTests: {
                src: [path + 'scripts/src/nicole.js'],
                options: {
                    specs: path + 'scripts/specs/**/*.js'
                    // vendor: 'resources/vendor/**/*.js'
                }
            }//,
            // coverage: {
            //   src: ['src/**/*.js'],
            //   options: {
            //        specs: ['specs/**/*.js'],
            //       vendor: 'vendor/**/*.js',
            //       template: require('grunt-template-jasmine-istanbul'),
            //       templateOptions: {
            //           coverage: 'bin/coverage/coverage.json',
            //           report: {
            //               type: 'html',
            //              options: {
            //                  dir: 'dist/report/coverage'
            //              }
            //          }
            //      }
            //     }
            // }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-jasmine');


    grunt.registerTask('default', ['jshint', 'jasmine:runTests', 'concat', 'uglify']);
    //grunt.registerTask('report', ['jasmine:runTests', 'jasmine:coverage']);
};

