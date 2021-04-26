Embulk::JavaPlugin.register_input(
  "kintone", "net.jp.ytake.embulk.input.kintone.KintoneInputPlugin",
  File.expand_path('../../../../classpath', __FILE__))
