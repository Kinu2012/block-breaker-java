# Block Breaker Java

Java Swingで作成した、シンプルなブロック崩しゲームです。

## Features

- 左右キーによるパドル操作
- ボールと壁・パドル・ブロックの当たり判定
- ゲーム開始、ゲームオーバー、ゲームクリアの状態管理
- ゲーム終了後のリトライ

## Requirements

- Java 21
- Maven 3.9以降

## Run

```sh
mvn compile exec:java
```

## Controls

| Key | Action |
| --- | --- |
| `Space` | ゲーム開始・リトライ |
| `←` / `→` | パドル移動 |

## Tech Stack

- Java 21
- Java Swing
- Maven
