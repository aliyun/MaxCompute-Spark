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
           MaxCompute Spark 使用文档
        </Heading>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="MaxCompute-Spark概述">
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
      title='MaxCompute Spark 使用文档'
      description="MaxCompute Spark 使用文档：了解如何使用 MaxCompute Spark 进行数据处理">
      <HomepageHeader />
    </Layout>
  );
}
