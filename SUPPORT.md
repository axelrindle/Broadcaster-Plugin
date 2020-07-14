# Server Software Policy

## Supported Versions

I officially support **Spigot or subsets of it**. When you'd like to request support for **another software** than Spigot (or subsets/forks of it), 
feel free to [open an issue](https://github.com/axelrindle/Broadcaster-Plugin/issues/new?title=Support+Request+for+SERVER_SOFTWARE_HERE).

Please be aware that support for older version might be dropped **at any time**. Assume that roughly the latest 4 version of Spigot (or subsets/forks of it) are supported. Also read [optional attributes](https://www.spigotmc.org/wiki/plugin-yml/#optional-attributes), specifically the **api-version** section. It states the following:

> This will signal to the server that your plugin has been coded with a specific server version in mind, and that it should not apply any sort of backwards compatibility measures. As a result you will also need to make sure that you have programmed your code to account for reading of older configurations, data, etc... .Each server version can decide how compatibility is achieved, unknown or future versions will prevent the plugin from enabling. As of the 1.14 release, the api-version 1.13 is still allowed - however future versions may drop backwards support based on this version.

Note that this document always applies to the **latest commit**. For support of historical version view the respective tree file.

| Spigot Version | Supported |
| ------- | ------------------ |
| 1.16.x   | :heavy_check_mark: |
| 1.15.x   | :heavy_check_mark: |
| 1.14.x   | :heavy_check_mark: |
| 1.13.x   | :heavy_check_mark: |
| <= 1.12.x   | :x: |

Symbol meanings:

| Symbol | Meaning |
| ------ | ------- |
| :heavy_check_mark: | Officially supported. This version is being actively tested. |
| :large_orange_diamond: | Should be generally supported, but it's not being tested. |
| :x: | Not supported. Stability and/or functionality is not guaranteed. |
| :construction: | Support for this version is currently in planning or already being tested. |

## Reporting issues or vulnerabilities

If you discovered an issue with any of the supported version, feel free to [open an issue](https://github.com/axelrindle/Broadcaster-Plugin/issues/new).
