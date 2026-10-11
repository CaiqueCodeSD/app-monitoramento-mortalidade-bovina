package com.example.myfirstapp.model

enum class CategoriaMortalidade(
    val descricao: String
) {
    DOENCAS_INFECCIOSAS_PARASITARIAS(
        "Doenças infecciosas e parasitárias"
    ),

    INTOXICACOES(
        "Intoxicações e causas tóxicas"
    ),

    ACIDENTES_TRAUMATICOS(
        "Acidentes e causas traumáticas"
    ),

    PREDACAO_E_ANIMAIS_PECONHENTOS(
        "Predação e animais peçonhentos"
    ),

    DISTURBIOS_DIGESTIVOS_METABOLICOS(
        "Distúrbios digestivos, nutricionais e metabólicos"
    ),

    OUTRAS_CAUSAS(
        "Outras causas"
    )
}