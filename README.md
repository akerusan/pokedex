# pokedex

課題の成功できた機能：  
・ポケモン一覧を表示する  
・無限スクロールさせ,offset を用いて 20 件ずつポケモンの一覧を表示する  
・項目をタップ時にポケモン詳細を表示する  
・オフライン時に過去に表示したポケモン一覧のキャッシュがある場合それを表示する  

利用した機能は下記のとおりです。  

＊開発言語:Kotlin  
＊MVVM アーキテクチャ  
＊Android Architecture Component  
→ LiveData  
→ ViewModel  
→ Room  
→ DataBinding  
＊Retrofit  
＊Coroutine (DBにデータをプッシュするときのみ)  
＊ConstraintLayout  

時間と現在の知識によりできなかったものは下記のとおりです。  

✖ dependency injection design pattern  
→ Dagger2まだ勉強中ですが、今回実装することができませんでした。  
（Koin も少し調べましたが実装する時間ができませんでした）  

✖ Unit Test  
→ こちらもまだ勉強中です。１つ実装してみましたがなかなか作ったテストがうまくいきませんでした。  

✖ View の生成に[epoxy](https://github.com/airbnb/epoxy)を使用する  

作成方法：  
使ったことのなかったものが多かったので下記順番に製造することにした。  
１．MVVMでretrofitを通じてAPIからデータ取得し、画面上表示。  
２．データベースとキャッシュを作成して、オフラインでも使える。  
３．データバインディング導入。  
４．DI（dagger2）->　勉強中で思ったより時間かかりそうと気づいてKoinを調査しましたが  
時間がなかったので削ることにしてUnitテストに進んでみました。  
５．Unitテスト -> 現在の業務で実装しているPTとIT（パイソンでの）とよく似ていて、調査が早かったですが、  
APIのテストがうまくいかず、viewModelのテストの時間ができませんでした。  
勉強続きます。  

苦労した点：  
このような全体的なアーキテクチャー（API＋DB）はじめてでしたので、1番苦労したことは全体的なイメージが途中までつかなかったことです。  
例えば、以上の作成方法の１．が終わった後に、DBを導入してviewModelとrepositoryを書き直すことになってしまいした。  


参考したサイト：  
https://developer.android.com/jetpack/docs/guide  
https://github.com/android/architecture-components-samples  
https://proandroiddev.com/android-architecture-starring-kotlin-coroutines-jetpack-mvvm-room-paging-retrofit-and-dagger-7749b2bae5f7  
https://codinginflow.com/  
https://codingwithmitch.com/  
https://www.google.com  
