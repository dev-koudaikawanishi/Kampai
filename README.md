# 🍻 Kampai 会社飲み会支援Webアプリ 仕様書

## 1. プロジェクト概要

| 項目       | 内容                          |
| -------- | --------------------------- |
| プロジェクト名  | Kampai                      |
| 対象ユーザー   | 社内の飲み会師、およびその参加者            |
| 目的       | 師業務の効率化（店探し・投票・予約・通知などを一元化） |
| プラットフォーム | Webアプリケーション（PC・スマホ両対応）      |

---

## 2. 機能一覧

### ☑ ️ 師向け機能

| 機能名     | 説明                               |
| ------- | -------------------------------- |
| 飲み会作成   | タイトル、日程候補、予算、人数などを入力して新規作成可能     |
| 店舗検索    | エリア・ジャンル・予算・人数・条件でお店を検索（API連携）   |
| 候補店登録   | 気になるお店を候補として登録し、投票用に共有可能         |
| 投票管理    | 投票の集計結果を確認し、決定したお店を確定できる         |
| 予約情報管理  | 確定したお店の予約内容（日時・人数）を記録            |
| リマインド通知 | Slack/LINEなどで参加者に通知（投票・確定・リマインド） |

### ☑ ️ 参加者向け機能

| 機能名     | 説明                      |
| ------- | ----------------------- |
| 招待URL参加 | 師から送られたURLから投票に参加できる    |
| 候補への投票  | 候補となっているお店から好きなものを選んで投票 |
| 結果確認    | 最終決定されたお店情報を参照できる       |

---

## 3. システム構成

| レイヤ     | 使用技術                          |
| ------- | ----------------------------- |
| フロントエンド | React + TypeScript + Tailwind |
| バックエンド  | Java (Spring Boot)            |
| データベース  | PostgreSQL                    |
| API連携   | ホットペッパー API, Google Maps API  |
| CI/CD   | GitHub Actions                |
| 実行環境    | Docker + docker-compose       |

---

## 4. REST API設計

### 🥂 飲み会関連

| メソッド   | エンドポイント            | 説明      |
| ------ | ------------------ | ------- |
| GET    | `/api/events`      | 飲み会一覧取得 |
| POST   | `/api/events`      | 飲み会作成   |
| GET    | `/api/events/{id}` | 詳細取得    |
| PUT    | `/api/events/{id}` | 飲み会更新   |
| DELETE | `/api/events/{id}` | 飲み会削除   |

### 🍽 候補店舗関連

| メソッド | エンドポイント                        | 説明      |
| ---- | ------------------------------ | ------- |
| POST | `/api/events/{id}/candidates`  | 候補店の追加  |
| GET  | `/api/events/{id}/candidates`  | 候補店一覧   |
| POST | `/api/events/{id}/vote`        | 候補店への投票 |
| GET  | `/api/events/{id}/vote/result` | 投票結果取得  |

---

## 5. データベース設計（概要）

| テーブル名        | 概要             |
| ------------ | -------------- |
| users        | 師・参加者ユーザー情報    |
| events       | 飲み会イベント情報      |
| candidates   | 候補店舗情報（イベントごと） |
| votes        | 投票情報（ユーザー×候補店） |
| reservations | 予約確定情報         |

---

## 6. 開発ロードマップ

* [ ] MVPリリース（検索・投票・決定）
* [ ] 招待URLの暗号化と有効期限機能
* [ ] Slack/LINE通知の切替機能
* [ ] Google Calendar連携
* [ ] 多言語対応（日本語/英語）

## Kampai データベース ER図 (Entity Relationship Diagram)

```mermaid
erDiagram
    USERS ||--o{ EVENTS : creates
    USERS ||--o{ VOTES : votes
    EVENTS ||--o{ CANDIDATES : has
    EVENTS ||--o{ VOTES : contains
    EVENTS ||--|| RESERVATIONS : confirms
    CANDIDATES ||--o{ VOTES : receives

    USERS {
        UUID id PK
        string name
        string email
        boolean is_organizer
        timestamp created_at
    }

    EVENTS {
        UUID id PK
        string title
        date date
        integer max_participants
        string location
        UUID created_by FK >- USERS.id
        timestamp created_at
    }

    CANDIDATES {
        UUID id PK
        UUID event_id FK >- EVENTS.id
        string name
        string address
        string genre
        string image_url
        timestamp created_at
    }

    VOTES {
        UUID id PK
        UUID user_id FK >- USERS.id
        UUID candidate_id FK >- CANDIDATES.id
        UUID event_id FK >- EVENTS.id
        integer vote_point
        timestamp voted_at
    }

    RESERVATIONS {
        UUID id PK
        UUID event_id FK >- EVENTS.id
        string reserved_place_name
        string reserved_time
        integer participant_count
        timestamp created_at
    }
```

> 備考：ER図はMermaid記法で記述しています。GitHubなどで表示させるには、対応プラグインまたは外部ツールをご利用ください。


# Kampai REST API 設計書

## ベースURL

```
/api
```

---

## 1. 飲み会関連エンドポイント

### GET /events

* 説明: 全ての飲み会イベントを取得
* レスポンス例:

```json
[
  {
    "id": "uuid",
    "title": "6月飲み会",
    "date": "2025-06-30",
    "location": "渋谷",
    "created_by": "user-uuid",
    "max_participants": 10
  }
]
```

### POST /events

* 説明: 飲み会イベントを新規作成
* リクエスト例:

```json
{
  "title": "歓迎会",
  "date": "2025-07-01",
  "location": "新宿",
  "max_participants": 15,
  "created_by": "user-uuid"
}
```

* レスポンス: 作成されたイベントの情報（HTTP 201）

### GET /events/{id}

* 説明: 特定イベントの詳細を取得

### PUT /events/{id}

* 説明: イベント情報の更新

### DELETE /events/{id}

* 説明: イベントの削除

---

## 2. 候補店舗関連エンドポイント

### GET /events/{id}/candidates

* 説明: 指定イベントに紐づく候補店舗を一覧取得

### POST /events/{id}/candidates

* 説明: 候補店舗を追加
* リクエスト例:

```json
{
  "name": "鳥貴族 新宿店",
  "address": "東京都新宿区...",
  "genre": "居酒屋",
  "image_url": "https://image.example.com/triki.jpg"
}
```

---

## 3. 投票関連エンドポイント

### POST /events/{id}/vote

* 説明: 指定イベントの候補店舗に投票
* リクエスト例:

```json
{
  "user_id": "user-uuid",
  "candidate_id": "candidate-uuid",
  "vote_point": 1
}
```

### GET /events/{id}/vote/result

* 説明: 投票結果を集計して取得（得票数順）

---

## 4. 予約情報

### GET /events/{id}/reservation

* 説明: イベントに紐づく予約情報を取得

### POST /events/{id}/reservation

* 説明: お店の予約情報を登録
* リクエスト例:

```json
{
  "reserved_place_name": "鳥貴族 新宿店",
  "reserved_time": "2025-07-01T19:00:00",
  "participant_count": 12
}
```

---

## 5. 参加者向け機能

### GET /events/{id}/invite/{token}

* 説明: 招待URLからイベントへアクセス

### POST /events/{id}/invite/{token}/vote

* 説明: 匿名ユーザーが招待URL経由で投票（user\_idは不要）


# Kampai UI モック案

## 🎉 トップページ（ログイン不要）

* タイトル：Kampai - 社内飲み会調整アプリ
* CTAボタン：

  * 「新しい飲み会を作成する」
  * 「招待リンクから参加する」

---

## 📝 飲み会作成ページ（幹事用）

* 入力項目：

  * 飲み会タイトル（text）
  * 開催候補日（複数選択可、カレンダー形式）
  * 人数上限（number）
  * エリア（セレクト or フリーテキスト）
  * 予算（セレクト or テキスト）
* 「飲み会を作成する」ボタン → 作成後に候補店検索画面へ遷移

---

## 🔍 候補店舗検索・登録ページ

* 検索条件

  * エリア、ジャンル、予算、人数
* 結果リスト（カード形式）

  * 店名、住所、ジャンル、サムネイル、詳細リンク
  * 「候補に追加」ボタン
* 候補リスト表示（サイド or 下部）

---

## 🗳 投票ページ（参加者・幹事共通）

* 飲み会名 + 開催予定日 + 人数
* 候補店一覧（チェック式 or 星評価）
* 投票ボタン
* 投票済みなら「結果を見る」リンクへ

---

## 📊 投票結果ページ

* 店舗ごとの得票数（棒グラフ or ランキング）
* 幹事は「この店を予約に決定」ボタン

---

## 📅 予約確認ページ（幹事のみ）

* 確定したお店情報

  * 店名、日時、住所、人数
* リマインド送信ボタン（Slack/LINE想定）

---

## ✅ 完了ページ（参加者用）

* 「飲み会は○月○日、△△店で確定しました！」
* カレンダーに追加 / SNSで共有 ボタン

---

> 備考：
> モバイルファーストで設計（Tailwind CSS）
> 色味は親しみやすい和風トーン（紺・山吹・白など）
