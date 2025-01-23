import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import Heading from '@theme/Heading';
import styles from './index.module.css';


/**
 * FIXME: 理论上有办法让用户直接进入到文档界面，而不是进入一个只有“进入文档”入口的标题界面。
 */
function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
           ODPS SDK for Java 使用文档
        </Heading>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="intro">
              进入文档 📚
          </Link>
        </div>
      </div>
    </header>
  );
}

export default function Home() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title='ODPS SDK for Java 使用文档'
      description="Java SDK文档：了解如何使用 odps-sdk-java 的SDK高效构建Java应用。">
      <HomepageHeader />
    </Layout>
  );
}
