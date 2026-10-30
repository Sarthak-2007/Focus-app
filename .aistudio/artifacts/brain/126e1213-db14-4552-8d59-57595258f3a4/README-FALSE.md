# DELETED as false (Issue #71, verified on e29acdf)

The satisfaction matrix and feature catalog formerly here stamped
VERIFIED/PASSED on View classes, fonts and ViewModel methods that do
not exist in app/src/main, and documented a removed mixer, Stamp
Press Studio, Curio Vault screen, 432 Hz synth and AES-GCM export as
shipped.

Truth lives in code:
- Views: BlockitCoreViews.kt:60,371,1444 /
  BlockitSuitcaseAndSwitchesViews.kt:32,190,532 /
  BlockitTowerFadersAndCatalogViews.kt:24,162 /
  MonolithRailView.kt:98 (8 concrete + 1 abstract View classes in 4
  files — not 15 dedicated files)
- Fonts: Type.kt:20,33,43 (Sora / Atkinson Hyperlegible /
  Anonymous Pro — Plus Jakarta Sans and JetBrains Mono are called
  "the old lying font aliases" at Type.kt:57-59)
- Screens: TactileDeckScreen.kt:96, ShieldMatrixScreen.kt:86,
  FocusSequencerScreen.kt:71, DrawerScreen.kt:134 (exactly 4)
- The deleted features: mixer and Stamp Press removed on Sarthak's
  order; AES-GCM backup export killed Oct 29, 2026
  (CryptoIntegrityManager.kt:17); CurioVaultScreen,
  StampPressStudioModal, FallingSquares grep 0 in app/src/main.
  Curios survive as data (CurioCatalog object, MonolithModels.kt:779; built at call sites MainActivity.kt:624-625, 799-809)
  rendered as Drawer cabinet rows (DrawerScreen.kt:160); the desk
  clock survives as an overlay (AirlockAndDisarmOverlays.kt:70 via
  MonolithViewModel.kt:1563 toggleAlwaysOnDeskClock).

The verbatim prompt archive (Part I of the deleted docs) is
preserved in git history at e29acdf. The other .aistudio documents
carry no verification authority.
