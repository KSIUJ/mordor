package pl.edu.uj.ii.ksi.mordor.model

class DirectoriesTreeNodeEntry(
    val name: String,
    val children: List<DirectoriesTreeNodeEntry>,
    val relativePath: String
)