miditools
=========

ヤマハの電子オルガン、ステージアのmidiの処理絡みで作った色々です。

- Bulk2Style バルクファイルの末端からMidiファイルの開始文字を探してユーザリズムスタイルを切り出します。
- MidiAddSysex midiの頭にシステムエクスクルーシブを付け足します。
- MidiRecord.java MidiExConvert.java (主にELSからの)midi信号を受け取ってリストにするための補助ツールです。

midiやステージアとpcのデータやり取りに関する基本的な解説をpdfにしたものは[ここ](http://user.ecc.u-tokyo.ac.jp/users/user-13080/elec/manual/els_pc.pdf)で配布しています。ステージアでXGサポートを自作したりさらに発展的な処理を手掛ける手始めにぜひ。
