這是一個功能完整的 `docker-compose.yml` 檔案，包含了您要求的 Kafka、Kafka-UI、Redis、Grafana 和 Prometheus。

這個設定將所有服務連接在一起，讓您可以輕鬆地監控和管理您的訊息隊列系統。

### 專案結構

為了讓設定檔案保持整潔，建議您建立以下資料夾結構：

```
.
├── docker-compose.yml
├── prometheus/
│   └── prometheus.yml
└── grafana/
    └── datasources/
        └── datasources.yml
```

---

### 1. `docker-compose.yml`

這是核心的編排檔案，定義了所有服務、網路和資料卷。

```yaml
version: '3.8'

services:
  # --- Kafka 依賴的 Zookeeper ---
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  # --- Kafka 訊息隊列 ---
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    hostname: kafka
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      # 外部連接端口 (PLAINTEXT)
      - "9092:9092"
      # 內部 Docker 網路通信端口 (PLAINTEXT_INTERNAL)
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      # 監聽器設定，讓 Kafka 可以處理來自不同來源的連接
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.32.128:9092,PLAINTEXT_INTERNAL://kafka:29092
      # 自動建立 topic
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1

  # --- Kafka 網頁管理介面 ---
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    depends_on:
      - kafka
    ports:
      - "8080:8080"
    environment:
      # 連接到上面定義的 Kafka 服務
      KAFKA_CLUSTERS_0_NAME: local-kafka-cluster
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper:2181

  # --- Redis 快取/資料庫 ---
  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  # --- Prometheus 監控指標收集 ---
  prometheus:
    image: prom/prometheus:v2.50.1
    container_name: prometheus
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
    ports:
      - "9090:9090"

  # --- Grafana 數據可視化 ---
  grafana:
    image: grafana/grafana:10.2.2
    container_name: grafana
    depends_on:
      - prometheus
    volumes:
      - ./grafana/datasources:/etc/grafana/provisioning/datasources
      - grafana_data:/var/lib/grafana
    ports:
      - "3000:3000"

volumes:
  redis_data:
  prometheus_data:
  grafana_data:

networks:
  default:
    name: monitoring-net
```

---

### 2. Prometheus 設定檔 (`prometheus/prometheus.yml`)

這個檔案告訴 Prometheus 要去哪裡抓取監控指標。我們將監控 Prometheus 自己、Kafka 和 Kafka-UI。

```yaml
global:
  scrape_interval: 15s # 每 15 秒抓取一次指標

scrape_configs:
  # 監控 Prometheus 自己
  - job_name: 'prometheus'
    static_configs:
      - targets: ['192.168.32.128:9090']

  # 監控 Kafka
  - job_name: 'kafka'
    static_configs:
      - targets: ['kafka:29092'] # 使用內部網路名稱和端口

  # 監控 Kafka-UI
  - job_name: 'kafka-ui'
    static_configs:
      - targets: ['kafka-ui:8080'] # 使用內部網路名稱和端口
```

---

### 3. Grafana 資料來源設定 (`grafana/datasources/datasources.yml`)

這個檔案會自動在 Grafana 中設定好 Prometheus 資料來源，這樣您就不用手動新增了。

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090 # 使用服務名稱連接到 Prometheus
    isDefault: true
    jsonData:
      # 可選：設定 Grafana 的 Explore 視圖預覽時間
      timeInterval: 15s
```

---

### 如何使用

1.  **建立檔案和資料夾**：
    依照上面的「專案結構」建立好 `docker-compose.yml`、`prometheus/prometheus.yml` 和 `grafana/datasources/datasources.yml` 這三個檔案。

2.  **啟動服務**：
    在專案根目錄下，打開終端機並執行：
    ```bash
    docker compose up -d
    ```
	或者
	```bash
	docker compose -f docker-compose.yml up -d
	```
    `-d` 參數會讓服務在背景運行。

3.  **檢查服務狀態**：
    等待幾分鐘讓所有服務啟動並穩定下來。您可以使用以下命令查看日誌，確保沒有錯誤：
    ```bash
    docker compose logs -f
    ```
	或者使用docker ps查看容器運行狀態
	```bash
	docker ps
	```

4.  **存取各個服務**：
    *   **Kafka-UI**: 打開瀏覽器，訪問 `http://192.168.32.128:8080`。您應該能看到 Kafka 集群的資訊。
    *   **Grafana**: 打開瀏覽器，訪問 `http://192.168.32.128:3000`。
        *   預設使用者名稱：`admin`
        *   預設密碼：`admin`
        *   登入後，您會發現 Prometheus 資料來源已經自動設定好了。您可以直接到 "Dashboards" 頁面匯入現成的 Kafka 監控範本（例如，在 [Grafana 官網](https://grafana.com/grafana/dashboards/) 搜尋 "Kafka"），然後選擇您的 Prometheus 資料來源即可。
    *   **Prometheus**: 打開瀏覽器，訪問 `http://192.168.32.128:9090`。您可以在這裡查詢指標，例如輸入 `kafka_broker_topic_messages_in_total` 來查看 Kafka 的訊息數量。
    *   **Redis**: 您可以使用任何 Redis 客戶端連接到 `192.168.32.128:6379`。

5.  **停止並清理**：
 當然可以。要停止所有由 `docker compose up` 啟動的容器，最直接且正確的方法是使用對應的 `down` 指令。

### 方法一：使用 `docker compose down` (推薦)

這個指令會停止並移除 `docker-compose.yml` 檔案中定義的所有服務（容器）、網路。這是最乾淨的清理方式。

**步驟如下**：

1.  **打開終端機**，並確保您位於包含 `docker-compose.yml` 檔案的目錄下。
    ```bash
    # 如果您不在該目錄，請使用 cd 指令切換
    cd /path/to/your/project
    ```

2.  **執行 `down` 指令**：
    ```bash
    docker compose down
    ```
    *如果您的檔案不叫 `docker-compose.yml`，您需要用 `-f` 參數指定檔案名稱：*
    ```bash
    docker compose -f docker-compose.yml down
    ```

**執行後會發生什麼事？**
您會看到類似以下的輸出，顯示 Docker 正在停止、移除容器和網路：
```
[+] Running 5/5
 ✔ Container zookeeper      Removed
 ✔ Container kafka-ui       Removed
 ✔ Container redis          Removed
 ✔ Container prometheus     Removed
 ✔ Network monitoring-net   Removed
```

---

### 方法二：使用 `docker compose stop` (僅停止，不移除)

如果您只是想暫時停止容器，而不是將它們完全移除，可以使用 `stop` 指令。

**指令**：
```bash
docker compose stop
```
或
```bash
docker compose -f docker-compose.yml stop
```

**這和 `down` 有什麼不同？**
*   `stop`：只會停止正在運行的容器，但容器本身、網路和資料卷都會被保留下來。您之後可以使用 `docker compose start` 來重新啟動它們。
*   `down`：會停止、**移除**容器和**移除**網路。這是一個更徹底的清理。

---

### 重要補充：如何一併移除資料卷 (Volumes)？

在您的 `docker-compose.yml` 範例中，我們定義了 `redis_data`、`prometheus_data` 和 `grafana_data` 這幾個具名資料卷 (named volumes)，用來持久化儲存數據。

預設情況下，`docker compose down` **不會**移除這些資料卷，以防止數據意外遺失。

如果您確定要**刪除所有數據**（例如 Prometheus 的監控指標、Grafana 的儀表板設定等），請在 `down` 指令後加上 `-v` (`--volumes`) 參數。

**指令**：
```bash
docker compose down -v
```

> **⚠️ 警告**： 這個指令會永久性地刪除所有在 `docker-compose.yml` 中定義的資料卷裡的數據。請在執行前確認您不再需要這些數據。

---

### 總結

| 指令 | 作用 | 適用情境 |
| :--- | :--- | :--- |
| `docker compose down` | **停止並移除**容器和網路。 | 最常用，徹底清理環境。 |
| `docker compose stop` | **僅停止**容器，保留所有資源。 | 暫時停止服務，稍後還要再啟動。 |
| `docker compose down -v` | **停止並移除**容器、網路和**資料卷**。 | 徹底重置，不保留任何數據。 |

對於您的需求「停止所有剛剛啟動的容器」，最推薦使用 `docker compose down`。

這個設定提供了一個強大且易於擴展的開發和監控環境。