// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config

import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'MaxCompute Spark',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  url: 'https://aliyun.github.io',
  baseUrl: '/MaxCompute-Spark/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'aliyun', // Usually your GitHub org/user name.
  projectName: 'MaxCompute-Spark', // Usually your repo name.
  trailingSlash: 'true',

  onBrokenAnchors: 'ignore',
  onBrokenLinks: 'ignore',
  onBrokenMarkdownLinks: 'ignore',

  markdown: {
      mermaid: true,
  },
  themes: ['@docusaurus/theme-mermaid'],

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'zh-Hans',
    locales: ['zh-Hans'],
  },

  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          routeBasePath: '/', // Serve the docs at the site's root
          sidebarPath: './sidebars.js',
        },
        blog: false,
        theme: {
          customCss: './src/css/custom.css',
        },
      },
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      docs: {
        sidebar: {
          hideable: true,
          autoCollapseCategories: true,
        },
      },
      image: 'img/logo.svg',
      navbar: {
        title: 'MaxCompute Spark',
        logo: {
          alt: 'MaxCompute Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'docs',
            position: 'left',
            label: '文档',
          },
//          {
//            href: 'https://github.com/aliyun/aliyun-odps-jdbc',
//            position: 'right',
//            label: '使用 JDBC 链接 MaxCompute',
//          },
//          {
//            type: 'docsVersionDropdown',
//            sidebarId: 'version',
//            position: 'left',
//            dropdownActiveClassDisabled: true,
//          },
//          {
//            href: 'https://github.com/aliyun/aliyun-odps-java-sdk',
//            label: 'GitHub',
//            position: 'right',
//          },
        ],
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: ['java'],
      },
    }),
};

export default config;

