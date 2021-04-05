Embulk::JavaPlugin.register_input(
  "kintone2", "org.embulk.input.kintone2.Kintone2InputPlugin",
  File.expand_path('../../../../classpath', __FILE__))
